import random
import numpy as np
from numba import cuda
from scipy.ndimage.filters import convolve as scipy_convolve
from timeit import timeit
from utils.image_utils import load_images_and_name_from_folder, save_image_to_folder


@cuda.jit
def convolve(result, mask, image, channel):
    # expects a 2D grid and 2D blocks,
    # a mask with odd numbers of rows and columns, (-1-)
    # a grayscale image

    # (-2-) 2D coordinates of the current thread:
    i, j = cuda.grid(2)

    # (-3-) if the thread coordinates are outside the image, we ignore the thread:
    image_rows, image_cols, _ = image.shape
    if (i >= image_rows) or (j >= image_cols):
        return

    # To compute the result at coordinates (i, j), we need to use delta_rows rows of the image
    # before and after the i_th row,
    # as well as delta_cols columns of the image before and after the j_th column:
    delta_rows = mask.shape[0] // 2
    delta_cols = mask.shape[1] // 2

    # The result at coordinates (i, j) is equal to
    # sum_{k, l} mask[k, l] * image[i - k + delta_rows, j - l + delta_cols]
    # with k and l going through the whole mask array:
    s = 0
    for k in range(mask.shape[0]):
        for l in range(mask.shape[1]):
            i_k = i - k + delta_rows
            j_l = j - l + delta_cols
            # (-4-) Check if (i_k, j_k) coordinates are inside the image:
            if (i_k >= 0) and (i_k < image_rows) and (j_l >= 0) and (j_l < image_cols):
                s += mask[k, l] * image[i_k, j_l, channel]
    result[i, j] = s


def apply_laplacian_filter():
    convolve[griddim, blockdim](d_result_channel_R, d_laPlacian, d_image, 0)
    convolve[griddim, blockdim](d_result_channel_B, d_laPlacian, d_image, 1)
    convolve[griddim, blockdim](d_result_channel_G, d_laPlacian, d_image, 2)


global output


def apply_scipy_filter():
    global output
    output = np.empty_like(image)
    laPlacian = [
        [[0, 0, 0], [1, 1, 1], [0, 0, 0]],
        [[1, 1, 1], [-4, -4, -4], [1, 1, 1]],
        [[0, 0, 0], [1, 1, 1], [0, 0, 0]],
    ]
    output = scipy_convolve(image, laPlacian, mode='constant', cval=0)


# Load images
folder_inputs = '..\\resources\\inputs'
folder_outputs = '..\\resources\\outputs'
images_names = load_images_and_name_from_folder(folder_inputs)
image, name = images_names[random.randint(0, len(images_names) - 1)]

# Mask matrix for LaPlacian filter (edge detection)
laPlacian = np.array([
    [0, 1, 0],
    [1, -4, 1],
    [0, 1, 0]
])

# We use blocks of 32x32 pixels:
blockdim = (32, 32)

# We compute grid dimensions big enough to cover the whole image:
griddim = (image.shape[0] // blockdim[0] + 1, image.shape[1] // blockdim[1] + 1)

# We get every colour channel of the image in order to apply convolution
red_channel = image[:, :, 0]
green_channel = image[:, :, 1]
blue_channel = image[:, :, 2]

# We create result channels
result_channel_R = np.empty_like(red_channel)
result_channel_G = np.empty_like(green_channel)
result_channel_B = np.empty_like(blue_channel)

# We transfer the data to the device memory, in order to calculate only the execution time
d_result_channel_R = cuda.to_device(result_channel_R)
d_result_channel_G = cuda.to_device(result_channel_G)
d_result_channel_B = cuda.to_device(result_channel_B)
d_laPlacian = cuda.to_device(laPlacian)
d_image = cuda.to_device(image)

# We apply our convolution to our image using the device and calculate the time:
print(timeit(lambda: apply_laplacian_filter(), number=1))

# We create back the full image
convolved_image = np.dstack((d_result_channel_R, d_result_channel_G, d_result_channel_B))
convolved_image = ((convolved_image - convolved_image.min()) * (
        1 / (convolved_image.max() - convolved_image.min()) * 255)).astype('uint8')

# We save our image to file
save_image_to_folder(folder_outputs, name, convolved_image)

# We apply our convolution to our image using the CPU
print(timeit(lambda: apply_scipy_filter(), number=1))

# We save our image to file
cpu_image_filename = name.split("\\")[-1].replace(".jpg", "-cpu.jpg")
splitted_name = name[:-1].join("\\")

save_image_to_folder(folder_outputs, splitted_name + cpu_image_filename, output)
