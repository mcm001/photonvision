#include <cstdio>

#include "casadi_meme.h"

#include <vector>
#include <iostream>

#include <Eigen/Dense>
#include <chrono>

using namespace std::chrono;

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

uint64_t nsec_solve = 0;

#define TAG_COUNT 3
#if TAG_COUNT < 1
	#error TAG_COUNT cannot be less than 1!
#endif

void print_cost(casadi_real robot_x, casadi_real robot_y, casadi_real robot_theta) {
	using StateMatrix = Eigen::Matrix<casadi_real, 3, 1, Eigen::ColMajor>;
	using HessianMat = Eigen::Matrix<casadi_real, 3, 3, Eigen::ColMajor>;
	using GradientMat = Eigen::Matrix<casadi_real, 3, 1>;

	casadi_real fx = 600;
	casadi_real fy = 600;
	casadi_real cx = 300;
	casadi_real cy = 150;

	constexpr int NUM_LANDMARKS = 4 * TAG_COUNT;
	
	// Note that casadi is column major, apparently
	Eigen::Matrix<casadi_real, 4, 4, Eigen::ColMajor> robot2camera;
	robot2camera <<
		0, 0, 1, 0,
		-1, 0, 0, 0,
		0, -1, 0, 0,
		0, 0, 0, 1;

	Eigen::Matrix<casadi_real, NUM_LANDMARKS, 4, Eigen::ColMajor> field2points_;
	field2points_ <<
		#if TAG_COUNT > 7
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		#if TAG_COUNT > 6
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		#if TAG_COUNT > 5
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		#if TAG_COUNT > 4
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		#if TAG_COUNT > 3
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		#if TAG_COUNT > 2
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		#if TAG_COUNT > 1
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1,
		#endif
		2.5, 0 - 0.08255, 0.5 - 0.08255, 1,
		2.5, 0 - 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 + 0.08255, 1,
		2.5, 0 + 0.08255, 0.5 - 0.08255, 1;
	Eigen::Matrix<casadi_real, 4, NUM_LANDMARKS, Eigen::ColMajor> field2points = field2points_.transpose();

	Eigen::Matrix<casadi_real, NUM_LANDMARKS, 2, Eigen::ColMajor> point_observations_;
	point_observations_ <<
		#if TAG_COUNT > 7
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		#if TAG_COUNT > 6
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		#if TAG_COUNT > 5
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		#if TAG_COUNT > 4
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		#if TAG_COUNT > 3
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		#if TAG_COUNT > 2
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		#if TAG_COUNT > 1
		333, -17,
		333, -83,
		267, -83,
		267, -17,
		#endif
		333, -17,
		333, -83,
		267, -83,
		267, -17;
	Eigen::Matrix<casadi_real, 2, NUM_LANDMARKS, Eigen::ColMajor> point_observations = point_observations_.transpose();

	// std::cout << "=====\nr:\n" << robot2camera << std::endl;
	// std::cout << "=====\nf:\n" << field2points << std::endl;
	// std::cout << "=====\np:\n" << point_observations << std::endl;

	StateMatrix x { robot_x, robot_y, robot_theta };

	auto begin = std::chrono::steady_clock::now();

	for (int iter = 0; iter < 100; iter++) {
		// std::cout << "iter " << iter << " x [" << x.transpose() << "]" << std::endl;

		const casadi_real *argv[] = {
			&x[0], &x[1], &x[2],
			&fx, &fy, &cx, &cy,
			robot2camera.data(),
			field2points.data(),
			point_observations.data()
		};

		HessianMat H;
		GradientMat g;

		{
			casadi_real *hess_j_out[] = {H.data()};
			if (hess_J(argv, hess_j_out, 0, 0, 0)) {
				printf("Failure!");
				return;
			}
		}
		{
			casadi_real *grad_j_out[] = {g.data()};
			if (grad_J(argv, grad_j_out, 0, 0, 0)) {
				printf("Failure!");
				return;
			}
		}

		auto H_ldlt = H.ldlt();
		if (H_ldlt.info() != Eigen::Success) {
			std::cerr << "LDLT decomp failed! H=" << std::endl << H << std::endl;
		}

		// Make sure H is positive definite (all eigenvalues are > 0)
		if ((H_ldlt.vectorD().array() <= 0.0).any()) {
			HessianMat delta_I = HessianMat::Identity() * 1e-5;

			// std::cout << "Eig(H)=[" << H_ldlt.vectorD().transpose() << "] not positive definite - regularizing" << std::endl;
			int j = 0;
			while ((H_ldlt.vectorD().array() <= 0.0).any()) {
				delta_I *= 10;
				H_ldlt = (H + delta_I).ldlt();

				j++;
			}

			// std::cout << "regularization tool " << j << " iters, delta_I=I*" << delta_I(0,0) << std::endl;
			H = H + delta_I;
		}

		StateMatrix p_x = H_ldlt.solve(-g);

		casadi_real old_cost;
		double alpha = 1.0;

		// Calculate our old cost before refinement
		{
			casadi_real *j_out[] = {&old_cost};
			if (J(argv, j_out, 0, 0, 0)) {
				printf("Failure!");
				return;
			}
		}

		// Iterate until our chosen trial_x doesn't actually increase our cost
		for (int alpha_refinement = 0; alpha_refinement < 100; alpha_refinement++) {
			StateMatrix trial_x = x + alpha * p_x;

			const casadi_real *trial_argv[] = {
				&trial_x[0], &trial_x[1], &trial_x[2],
				&fx, &fy, &cx, &cy,
				robot2camera.data(),
				field2points.data(),
				point_observations.data()
			};

			casadi_real new_cost;
			{
				casadi_real *j_out[] = {&new_cost};
				if (J(trial_argv, j_out, 0, 0, 0)) {
					printf("Failure!");
					return;
				}
			}

			if (new_cost < old_cost) {
				x = trial_x;
				break;
			} else {
				alpha *= 0.5;
			}
		}

		if (g.norm() < 1e-8) {
			auto end = steady_clock::now();
			nsec_solve += duration_cast<nanoseconds>(end - begin).count();

			std::cout << "Converged! x=\n" << x << std::endl;
			break;
		}
	}


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
	for (int i = 0; i < total; i++) print_cost(0.2,-.3,0.1);
	printf("Total time,n=%i:\n\nsolve=%ins\n", total, nsec_solve/total);
}
