#include <cstdio>

#include "casadi_meme.h"

#include <vector>
#include <iostream>

#include <Eigen/Dense>

// Compile with: 
// /home/matt/photonvision/.venv/bin/python /home/matt/photonvision/scripts/casadi_meme.py && g++ -g -Og -Wall -std=c++20 casadi_meme.c main.cpp && ./a.out 

void print_sparsity(const casadi_int sparsity[]) {
    int nrow = sparsity[0];
    int ncol = sparsity[1];
    if (sparsity[2] == 1) {
        //dense
        printf("dense: mat is %ix%i", nrow, ncol);
    } else if (sparsity[2] == 0) {
        printf("mat is sparse, %ix%i\n", nrow, ncol);

        std::vector<int> colind{sparsity+2, sparsity+2+ncol};
        int nnz = sparsity[2+ncol];

        for (const auto c : colind) {
            printf("i=%i ", c);
        }
        printf("nnz=%i", nnz);
    } else {
        printf("wtffff");
    }

    printf("\n");
}

int main() {
    for (int i = 0; i < J_n_in(); i++) {
        printf("arg in: %s, ", J_name_in(i));
        print_sparsity(J_sparsity_in(i));
    }

    for (int i = 0; i < J_n_out(); i++) {
        printf("arg out: %s, ", J_name_out(i));
        print_sparsity(J_sparsity_out(i));
    }

    double robot_x = 0;
    double robot_y = 0;
    double robot_theta = 0;
    double fx = 600;
    double fy = 600;
    double cx = 300;
    double cy = 150;
    
    // Note that casadi is column major, apparently
    Eigen::Matrix<double, 4, 4, Eigen::ColMajor> robot2camera;
    robot2camera <<
        0, 0, 1, 0,
        -1, 0, 0, 0,
        0, -1, 0, 0,
        0, 0, 0, 1;

    Eigen::Matrix<double, 4, 4, Eigen::ColMajor> field2points_;
    field2points_ <<
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1;
    Eigen::Matrix<double, 4, 4, Eigen::ColMajor> field2points = field2points_.transpose();

    Eigen::Matrix<double, 4, 2, Eigen::ColMajor> point_observations_;
    point_observations_ <<
        333, -17,
        333, -83,
        267, -83,
        267, -17;
    Eigen::Matrix<double, 2, 4, Eigen::ColMajor> point_observations = point_observations_.transpose();

    std::cout << "=====\nr:\n" << robot2camera << std::endl;
    std::cout << "=====\nf:\n" << field2points << std::endl;
    std::cout << "=====\np:\n" << point_observations << std::endl;

    // cost
    double j;

    const casadi_real *argv[] = {
        &robot_x, &robot_y, &robot_theta,
        &fx, &fy, &cx, &cy,
        robot2camera.data(),
        field2points.data(),
        point_observations.data()
    };
    casadi_real *ret[] = {&j};

    if (J(argv, ret, 0, 0, 0)) {
        printf("Failure! J=%f\n", j);
        return -1;
    }

    printf("Success! J=%f\n", j);

    return 0;
}
