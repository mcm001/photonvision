from jormungandr.autodiff import VariableMatrix, Variable, sin, cos, solve
from jormungandr.optimization import OptimizationProblem
import numpy as np

problem = OptimizationProblem()

# camera calibration
fx = 600
fy = 600
cx = 300
cy = 150

# robot pose
robot_x = problem.decision_variable()
robot_y = problem.decision_variable()
robot_z = Variable(0)
robot_θ = problem.decision_variable()

# cache autodiff variables
sinθ = sin(robot_θ)
cosθ = cos(robot_θ)
_0 = Variable(0)
_1 = Variable(1)

field2robot = VariableMatrix(
    [
        [cosθ, -sinθ, _0, robot_x],
        [sinθ, cosθ, _0, robot_y],
        [_0, _0, _1, robot_z],
        [_0, _0, _0, _1],
    ]
)

# robot is ENU, cameras are SDE
robot2camera = np.array(
    [
        [0, 0, 1, 0],
        [-1, 0, 0, 0],
        [0, -1, 0, 0],
        [0, 0, 0, 1],
    ]
)

field2camera: VariableMatrix = field2robot @ robot2camera

# Cost
J = 0.0

# list of points in field space to reproject. Each one is a 4x1 vector of (x,y,z,1)
field2points = [
    VariableMatrix([[1.5, 0 - 0.08255, 0.5 - 0.08255, 1]]).T,
    VariableMatrix([[1.5, 0 - 0.08255, 0.5 + 0.08255, 1]]).T,
    VariableMatrix([[1.5, 0 + 0.08255, 0.5 + 0.08255, 1]]).T,
    VariableMatrix([[1.5, 0 + 0.08255, 0.5 - 0.08255, 1]]).T,
]

# List of points we saw the target at. These are exactly what we expect for a camera located at 0,0,0 (hand-calculated)
point_observations = [
    (333, -17),
    (333, -83),
    (267, -83),
    (267, -17),
]

# initial guess at robot pose. We expect the robot to converge to 0,0,0
robot_x.set_value(-0.01)
robot_y.set_value(0.0)
robot_θ.set_value(0.02)

# field2camera @ field2camera^-1 = I
camera2field = solve(field2camera, VariableMatrix(np.identity(4)))

for field2point, observation in zip(field2points, point_observations):
    camera2point = camera2field @ field2point

    # point's coordinates in camera frame
    x = camera2point[0]
    y = camera2point[1]
    z = camera2point[2]

    # print(f"field2point =\n{field2point.value()}")
    # print(f"Camera2point = {x.value()}, {y.value()}, {z.value()}")

    # coordinates observed at
    u_observed, v_observed = observation

    X = x / z
    Y = y / z

    u = fx * X + cx
    v = fy * Y + cy

    # print(f"Expected: uv {round(u.value())}, {round(v.value())}")
    # print(f"saw: uv {u_observed}, {v_observed}")

    u_err = u - u_observed
    v_err = v - v_observed

    # Cost function is square of reprojection error
    J += u_err * u_err + v_err * v_err

problem.minimize(J)

problem.solve(diagnostics=True)

print(
    f"X={round(robot_x.value(), 5)} m, Y={round(robot_x.value(), 5)} m, theta={round(robot_θ.value(), 5)} rad"
)
