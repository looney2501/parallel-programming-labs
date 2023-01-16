import glob

import requests
import os
from tqdm import tqdm
import shutil
import skimage.io
import matplotlib.pyplot as plt

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36'
}

def generate_images(no_images, output_folder):
    # create folder_inputs
    cur_dir = os.getcwd()
    output = f'{os.path.abspath(os.path.join(cur_dir, os.pardir))}\\resources\\{output_folder}'
    if os.path.exists(output):
        shutil.rmtree(output)
    os.mkdir(output)

    # make Unsplash request
    api_url = f'https://unsplash.com/napi/photos/random?count={no_images}'
    response = requests.get(api_url, headers=headers)
    response_body = response.json()
    for image in tqdm(response_body):
        image_title = image['alt_description']
        image_url = image['urls']['raw']
        image_response = requests.get(image_url, stream=True)
        image = image_response.content

        # write image in folder_inputs
        with open(output + f'\\{image_title}.jpg', 'wb') as file:
            file.write(image)

if __name__ == '__main__':
    generate_images(5, 'inputs')


def load_images_and_name_from_folder(folder):
    images_names = []
    for filename in glob.glob(folder + '/*.jpg'):
        img = skimage.io.imread(filename)
        # img = skimage.io.imread(filename, as_gray=True)
        if img is not None:
            images_names.append((img, filename))
    return images_names


def save_image_to_folder(folder, name, image):
    path = folder + '\\' + name.split("\\")[-1].replace(".jpg", "-filter.jpg")
    skimage.io.imsave(path, image)
