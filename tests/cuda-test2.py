from timeit import timeit
import numpy as np
from numba import cuda, jit

arr = np.random.randn(2048 * 1024).astype(np.float32)
coeffs = np.float32(range(1, 10))

@jit
def host_polyval(result, array, coeffs):
    for i in range(len(array)):
        val = coeffs[0]
        for coeff in coeffs[1:]:
            val = val * array[i] + coeff
        result[i] = val

result = np.empty_like(arr)

print(timeit(lambda: host_polyval(result, arr, coeffs)))

@cuda.jit
def cuda_polyval(result, arr, coeffs):
    i = cuda.grid(1)
    val = coeffs[0]
    for coeff in coeffs[1:]:
        val = val * arr[i] + coeff
    result[i] = val

result = np.empty_like(arr)

d_array = cuda.to_device(arr)
d_coeffs = cuda.to_device(coeffs)
d_result = cuda.to_device(result)

print(timeit(lambda: cuda_polyval[2048, 1024](d_result, d_array, d_coeffs)))
