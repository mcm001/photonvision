import casadi as ca

# Symbolic variables
u = ca.SX.sym('u')
v = ca.SX.sym('v')
fx = ca.SX.sym('fx')
fy = ca.SX.sym('fy')
cx = ca.SX.sym('cx')
cy = ca.SX.sym('cy')
# Eight radial, two tangential
k = ca.SX.sym('k', 8)  # [k1 k2 k3 k4 k5 k6 k7 k8]
p1 = ca.SX.sym('p1')
p2 = ca.SX.sym('p2')

# Distorted normalized coordinates
xd = (u - cx) / fx
yd = (v - cy) / fy

# Initial guess: undistorted = distorted
x = xd
y = yd

# Iterative undistortion (e.g., 5 fixed-point iterations)
for i in range(5):
    r2 = x*x + y*y
    r4 = r2*r2
    r6 = r4*r2
    r8 = r4*r4
    D = (1 
         + k[0]*r2 + k[1]*r4 + k[2]*r6 + k[3]*r8
         + k[4]*r2*r4 + k[5]*r2*r6 + k[6]*r2*r8 + k[7]*r4*r6)  # Higher order terms
    delta_x = 2*p1*x*y + p2*(r2 + 2*x*x)
    delta_y = p1*(r2 + 2*y*y) + 2*p2*x*y
    x = (xd - delta_x) / D
    y = (yd - delta_y) / D

undistorted = ca.vertcat(x, y)

# Create a CasADi function
undistort_func = ca.Function(
    'undistort_point_opencv8',
    [u, v, fx, fy, cx, cy, k, p1, p2],
    [undistorted],
    ['u', 'v', 'fx', 'fy', 'cx', 'cy', 'k', 'p1', 'p2'],
    ['xy']
)

# Code generation
C = ca.CodeGenerator('undistort_opencv8.c')
C.add(undistort_func)
C.generate()
