#include <cstdio>

#include "casadi_meme.h"

#include <vector>
#include <iostream>

#include <Eigen/Dense>
#include <chrono>

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

int nsec_J = 0;
int nsec_grad = 0;
int nsec_hess = 0;

void print_cost(casadi_real robot_x, casadi_real robot_y, casadi_real robot_theta) {
    casadi_real fx = 600;
    casadi_real fy = 600;
    casadi_real cx = 300;
    casadi_real cy = 150;
    
    // Note that casadi is column major, apparently
    Eigen::Matrix<casadi_real, 4, 4, Eigen::ColMajor> robot2camera;
    robot2camera <<
        0, 0, 1, 0,
        -1, 0, 0, 0,
        0, -1, 0, 0,
        0, 0, 0, 1;

    Eigen::Matrix<casadi_real, 32, 4, Eigen::ColMajor> field2points_;
    field2points_ <<
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 - 0.08255, 1,
        1.5, 0 - 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 + 0.08255, 1,
        1.5, 0 + 0.08255, 0.5 - 0.08255, 1;
    Eigen::Matrix<casadi_real, 4, 32, Eigen::ColMajor> field2points = field2points_.transpose();

    Eigen::Matrix<casadi_real, 32, 2, Eigen::ColMajor> point_observations_;
    point_observations_ <<
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17,
        333, -17,
        333, -83,
        267, -83,
        267, -17;
    Eigen::Matrix<casadi_real, 2, 32, Eigen::ColMajor> point_observations = point_observations_.transpose();

    // std::cout << "=====\nr:\n" << robot2camera << std::endl;
    // std::cout << "=====\nf:\n" << field2points << std::endl;
    // std::cout << "=====\np:\n" << point_observations << std::endl;

    // cost
    casadi_real j;

    const casadi_real *argv[] = {
        &robot_x, &robot_y, &robot_theta,
        &fx, &fy, &cx, &cy,
        robot2camera.data(),
        field2points.data(),
        point_observations.data()
    };

    casadi_real *j_out[] = {&j};
    Eigen::Matrix<casadi_real, 1, 3> grad_J_mat;
    casadi_real *grad_j_out[] = {grad_J_mat.data()};
    Eigen::Matrix<casadi_real, 3, 3, Eigen::ColMajor> hess_J_mat;
    casadi_real *hess_j_out[] = {hess_J_mat.data()};


    std::chrono::steady_clock::time_point begin = std::chrono::steady_clock::now();
    if (J(argv, j_out, 0, 0, 0)) {
        printf("Failure!");
        return;
    }
    std::chrono::steady_clock::time_point end = std::chrono::steady_clock::now();
    nsec_J += std::chrono::duration_cast<std::chrono::nanoseconds>(end - begin).count();

    begin = std::chrono::steady_clock::now();
    if (grad_J(argv, grad_j_out, 0, 0, 0)) {
        printf("Failure!");
        return;
    }
    end = std::chrono::steady_clock::now();
    nsec_grad += std::chrono::duration_cast<std::chrono::nanoseconds>(end - begin).count();

    begin = std::chrono::steady_clock::now();
    if (hess_J(argv, hess_j_out, 0, 0, 0)) {
        printf("Failure!");
        return;
    }
    end = std::chrono::steady_clock::now();
    nsec_hess += std::chrono::duration_cast<std::chrono::nanoseconds>(end - begin).count();

    // printf("Success! x=%f,%f,%f\nJ=%f\n", robot_x, robot_y, robot_theta, j);
    // std::cout << "gradient: [" << grad_J_mat << "]" << std::endl;
    // std::cout << "hessian:\n" << hess_J_mat << std::endl;
    // printf("==========\n");
}

int main() {
    // for (int i = 0; i < J_n_in(); i++) {
    //     printf("arg in: %s, ", J_name_in(i));
    //     print_sparsity(J_sparsity_in(i));
    // }

    // for (int i = 0; i < J_n_out(); i++) {
    //     printf("arg out: %s, ", J_name_out(i));
    //     print_sparsity(J_sparsity_out(i));
    // }

    // print_cost(0,0,0);
    // print_cost(0.1,0,0);
    // print_cost(0,0.2,0);
    int total = 100;
    for (int i = 0; i < total; i++) print_cost(0.1,0.2,0.3);
    printf("Total time,n=%i:\n\nJ=%ins\ngrad(J)=%ins\nhess(J)=%ins\n", total, nsec_J/total, nsec_grad/total,nsec_hess/total);
}