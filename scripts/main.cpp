#include <cstdio>

#define casadi_real double
#define casadi_int long long int

// Compile with: g++ -O3 *.c -std=c++20 main.cpp && ./a.out 

extern "C" {
    // cost, gradient and hessian
    int J(const casadi_real** arg, casadi_real** res, casadi_int* iw, casadi_real* w, int mem);
    int grad_J(const casadi_real** arg, casadi_real** res, casadi_int* iw, casadi_real* w, int mem);
    int hess_J(const casadi_real** arg, casadi_real** res, casadi_int* iw, casadi_real* w, int mem);
}

void transpose(int rows, int cols, double *arr, double *result) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            result[j * rows + i] = arr[i * cols + j];
        }
    }
}

int main() {
    double x[] = {0.0, 0.0, 0.0};
    double fx = 600;
    double fy = 600;
    double cx = 300;
    double cy = 150;
    
    // Note that casadi is column major, apparently
    double robot2camera_[16] = {
        0, 0, 1, 0,
        -1, 0, 0, 0,
        0, -1, 0, 0,
        0, 0, 0, 1,
    };
    double robot2camera[16];

    double field2points_[4*4] = {
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
    };
    double field2points[4*4];

    double point_observations_[2*4] = {
        333, -17,
        333, -83,
        267, -83,
        267, -17,
    };
    double point_observations[2*4] = {
        point_observations_[0],
        point_observations_[2],
        point_observations_[4],
        point_observations_[6],
        point_observations_[1],
        point_observations_[3],
        point_observations_[5],
        point_observations_[7],
    };

    transpose(4, 4, robot2camera_, robot2camera);
    transpose(4, 4, field2points_, field2points);

    // cost
    double j;

    const casadi_real *argv[] = {
        x, &fx, &fy, &cx, &cy, robot2camera, field2points, point_observations
    };
    casadi_real *ret[] = {&j};

    if (J(argv, ret, 0, 0, 0)) {
        printf("Failure! J=%f\n", j);
        return -1;
    }

    printf("Success! J=%f\n", j);

    return 0;
}
