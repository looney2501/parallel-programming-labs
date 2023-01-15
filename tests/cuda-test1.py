from numba import cuda
import numpy as np

@cuda.jit
def cuda_kernel(arr):
    thread_position = cuda.blockIdx.x * cuda.blockDim.x + cuda.threadIdx.x
    arr[thread_position] += 0.5

arr = np.zeros(1024 * 1024, np.float32)
print('Initial array:', arr)

print('Kernel launch: cudakernel1[1024, 1024](array)')
cuda_kernel[(1024, 1, 1), (1024, 1, 1)](arr)

print('Updated array:', arr)

# Since it is a huge array, let's check that the result is correct:
print('The result is correct:', np.all(arr == np.zeros(1024 * 1024, np.float32) + 0.5))
