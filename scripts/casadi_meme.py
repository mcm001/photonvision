import builtins
import casadi as ca
from casadi import *
from matplotlib import pyplot as plt
from numpy import *
from os import system
import time
import sys
from casadi import casadi
import scipy as sp

# Camera calibration parameters
fx = ca.SX.sym("fx")
fy = ca.SX.sym("fy")
cx = ca.SX.sym("cx")
cy = ca.SX.sym("cy")

# Decision variables
robot_x = ca.SX.sym("robot_x")
robot_y = ca.SX.sym("robot_y")
robot_z = 0  # Fixed at 0
robot_θ = ca.SX.sym("robot_θ")

# Precompute trigonometric functions
sinθ = ca.sin(robot_θ)
cosθ = ca.cos(robot_θ)

# Transformation matrices
field2robot = ca.vertcat(
    ca.horzcat(cosθ, -sinθ, 0, robot_x),
    ca.horzcat(sinθ, cosθ, 0, robot_y),
    ca.horzcat(0, 0, 1, robot_z),
    ca.horzcat(0, 0, 0, 1),
)

robot2camera_ = ca.DM(
    [
        [0, 0, 1, 0],
        [-1, 0, 0, 0],
        [0, -1, 0, 0],
        [0, 0, 0, 1],
    ]
)

robot2camera = ca.SX.sym("robot2camera", 4, 4)

field2camera = field2robot @ robot2camera

NUM_LANDMARKS = 4 * 1

# Points in the field (homogeneous coordinates). Rows are [x, y, z, 1]
field2points = ca.SX.sym("field2landmark", 4, NUM_LANDMARKS)

# Observed points in the image
point_observations = ca.SX.sym("observations_px", 2, NUM_LANDMARKS)

# landmarks in camera frame
camera2field = ca.inv(field2camera)
camera2point = camera2field @ field2points

# Camera frame coordinates
x = camera2point[0, :]
y = camera2point[1, :]
z = camera2point[2, :]

# Observed coordinates
u_observed = point_observations[0, :]
v_observed = point_observations[1, :]

# Project to image plane
X = x / z
Y = y / z

u = fx * X + cx
v = fy * Y + cy

# Reprojection error
u_err = u - u_observed
v_err = v - v_observed

# Frobenius norm - sqrt(sum squared of each component). Square to remove sqrt
J = ca.norm_fro(u_err)**2 + ca.norm_fro(v_err)**2

# print(J)


SOLVE_IPOPT = False
if SOLVE_IPOPT:
    # Define optimization problem
    nlp = {"x": x_vec, "f": J}
    solver = ca.nlpsol("solver", "ipopt", nlp)

    # Initial guess
    x0 = ca.DM([-0.1, 0.0, 0.2])

    # Solve the problem
    sol = solver(x0=x0)

    # Extract solution
    robot_x_sol = float(sol["x"][0])
    robot_y_sol = float(sol["x"][1])
    robot_θ_sol = float(sol["x"][2])

    # Print results
    print(f"X={robot_x_sol:.5f} m, Y={robot_y_sol:.5f} m, theta={robot_θ_sol:.5f} rad")

x_vec = ca.vertcat(robot_x, robot_y, robot_θ)

# Hessian + gradient
hess_J, _ = ca.hessian(J, x_vec)
grad_J = ca.gradient(J, x_vec)

# Cost, plus grad and hessian of cost
J_func = ca.Function(
    "J", 
    [robot_x, robot_y, robot_θ, fx, fy, cx, cy, robot2camera, field2points, point_observations], 
    [J],
    ["robot_x", "robot_y", "robot_θ", "fx", "fy", "cx", "cy", "robot2camera", "field2points", "point_observations"], 
    ["J"],
)
grad_func = ca.Function(
    "grad_J",
    [robot_x, robot_y, robot_θ, fx, fy, cx, cy, robot2camera, field2points, point_observations],
    [grad_J],
    ["robot_x", "robot_y", "robot_θ", "fx", "fy", "cx", "cy", "robot2camera", "field2points", "point_observations"], 
    ["grad_J"],
)
hess_func = ca.Function(
    "hess_J",
    [robot_x, robot_y, robot_θ, fx, fy, cx, cy, robot2camera, field2points, point_observations],
    [hess_J],
    ["robot_x", "robot_y", "robot_θ", "fx", "fy", "cx", "cy", "robot2camera", "field2points", "point_observations"], 
    ["hess_J"],
)

print(J_func)
print(grad_func)
print(hess_func)

if True:
    robot2camera = np.array(
        [
            [0, 0, 1, 0],
            [-1, 0, 0, 0],
            [0, -1, 0, 0],
            [0, 0, 0, 1],
        ]
    )

    # list of points in field space to reproject. Each one is a 4x1 vector of (x,y,z,1)
    field2points = np.array([
        [1.5, 0 - 0.08255, 0.5 - 0.08255, 1],
        [1.5, 0 - 0.08255, 0.5 + 0.08255, 1],
        [1.5, 0 + 0.08255, 0.5 + 0.08255, 1],
        [1.5, 0 + 0.08255, 0.5 - 0.08255, 1],
    ]).T
    
    point_observations = np.array([
        (333, -17),
        (333, -83),
        (267, -83),
        (267, -17),
    ]).T

    # x = [0,0,0]
    # print(f"J({x})={J_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print(f"grad({x})={grad_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print(f"hess({x})={hess_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print("============")
    # x = [0.1,0,0]
    # print(f"J({x})={J_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print(f"grad({x})={grad_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print(f"hess({x})={hess_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print("============")
    # x = [0,0.2,0]
    # print(f"J({x})={J_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print(f"grad({x})={grad_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print(f"hess({x})={hess_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    # print("============")
    x = [0.1,0.2,0.3]
    print(f"J({x})={J_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    print(f"grad({x})={grad_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    print(f"hess({x})={hess_func(*x, 600, 600, 300, 150, robot2camera, field2points, point_observations)}")
    print("============")

if False:

    x_ = np.linspace(-0.1, 0.1, 100)
    y_ = np.linspace(-0.1, 0.1, 100)
    # X, Y = np.meshgrid(x,y)

    J = np.zeros((100, 100))
    gx = np.zeros((100, 100))
    gy = np.zeros((100, 100))
    gt = np.zeros((100, 100))

    for xidx, x in enumerate(x_):
        for yidx, y in enumerate(y_):
            J_ = J_func(np.array([x, y, 0]))
            g_ = grad_func(np.array([x, y, 0]))
            H_ = hess_func(np.array([x, y, 0]))
            J[xidx, yidx] = float(J_)
            gx[xidx, yidx] = float(g_[0, 0])
            gy[xidx, yidx] = float(g_[1, 0])
            gt[xidx, yidx] = float(g_[2, 0])

    plt.figure(1)
    plt.imshow(gx, interpolation="nearest")
    plt.colorbar()
    plt.title("ΔJ_x vs robot pose")
    plt.xlabel("x (m)")
    plt.ylabel("y (m)")

    plt.figure(2)
    plt.imshow(gy, interpolation="nearest")
    plt.colorbar()
    plt.title("ΔJ_y vs robot pose")
    plt.xlabel("x (m)")
    plt.ylabel("y (m)")

    plt.figure(3)
    plt.imshow(gt, interpolation="nearest")
    plt.colorbar()
    plt.title("ΔJ_theta vs robot pose")
    plt.xlabel("x (m)")
    plt.ylabel("y (m)")

    plt.show()

SOLVE_NEWTON = True
if SOLVE_NEWTON:
    # Newton's method parameters
    x0 = np.array([-0.1, 0.0, 0.2]).reshape((3, 1))  # Initial guess
    tol = 1e-8  # Convergence tolerance
    max_iter = 100  # Maximum number of iterations

    fx = 600
    fy = 600
    cx = 300
    cy = 150

    # Newton's method loop
    x = x0
    error = float("inf")
    for i in range(max_iter):
        grad = np.array(grad_func(*x, fx, fy, cx, cy, robot2camera, field2points, point_observations))
        hess = np.array(hess_func(*x, fx, fy, cx, cy, robot2camera, field2points, point_observations))

        L, D, perm = sp.linalg.ldl(hess)

        delta_I = np.zeros(hess.shape)
        if np.min(np.diag(D)) <= 0:
            # until the values in D are all positive
            delta_I = np.eye(hess.shape[0]) * 1e-4

            while np.min(np.diag(D)) <= 0:
                delta_I *= 10
                hess_copy = hess + delta_I

                L, D, perm = sp.linalg.ldl(hess_copy)

        p_x = np.linalg.solve(hess + delta_I, -grad)

        oldCost = J_func(*x, fx, fy, cx, cy, robot2camera, field2points, point_observations)
        alpha = 1.0
        trial_x = x + alpha * p_x

        # hack
        while J_func(*trial_x, fx, fy, cx, cy, robot2camera, field2points, point_observations) > oldCost:
            alpha *= 0.5
            trial_x = x + alpha * p_x
        print(
            f"Used alpha={alpha}, x={list(x)}, x_n=1={list(trial_x)}, dx={list(alpha * p_x)}"
        )
        x = trial_x

        print(grad)
        if np.linalg.norm(grad) <= 1e-8:
            print("DONE!")
            break

    robot_x_sol, robot_y_sol, robot_θ_sol = x
    print(f"X={robot_x_sol} m, Y={robot_y_sol} m, theta={robot_θ_sol} rad")

cg = CodeGenerator('casadi_meme', {
    'with_header': True,
    'cpp': False,
})
cg.add(J_func)
cg.add(grad_func)
cg.add(hess_func)
cg.generate()

# print('Compiling with O3 optimization: ', oname_O3)
# t1 = time.time()
# system('gcc -fPIC -shared ' + cname + ' -o ' + oname_O3)
# t2 = time.time()
# print('time = ', (t2-t1)*1e3, ' ms')

exit(0)

# Read function
grad_det_O3 = external(name, "./" + oname_O3)

# Random point to evaluate it
x0 = DM.rand(7, 7)

t1 = time.time()
nrep = 10000
for r in range(nrep):
    r = grad_det_O3(x0)
t2 = time.time()
print("result = ", r.nonzeros())
dt = (t2 - t1) / nrep
print("time = ", dt * 1e3, " ms")

num_op = grad_det.n_nodes()
print("number of elementary operations: ", num_op)
print("time per elementary operations: ", dt / num_op * 1e9, " ns")
