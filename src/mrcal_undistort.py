import numpy as np
import cv2
import mrcal

def main():
    # Example camera intrinsics
    fx, fy = 800, 800   # focal lengths
    cx, cy = 320, 240   # principal point

    # Use 3-coefficient radial distortion (OpenCV's k1, k2, p1, p2, k3 form)
    # (values chosen arbitrarily; positive k1 means barrel distortion)
    dist_coeffs = np.array([0.3, -0.5, 0.01, 0.05, 0.1, -0.1, 0.1, -.25])
    camera_matrix = np.array([[fx,  0, cx],
                              [ 0, fy, cy],
                              [ 0,  0,  1]], dtype=float)

    # 2D distorted test points in pixels (N,1,2) for OpenCV
    pts = np.array([[[0, 0]],
                    [[700, 400]]], dtype=float)
    print('Test points (distorted):')
    print(pts.reshape(-1, 2))

    # OpenCV undistort (returns normalized coordinates)
    pts_cv_undist = cv2.undistortImagePoints(pts, camera_matrix, dist_coeffs, arg1=(cv2.TERM_CRITERIA_EPS | cv2.TERM_CRITERIA_COUNT, 30, 1e-6))
    print('\nOpenCV undistorted (pixels):')
    print(pts_cv_undist.reshape(-1, 2))

    # ----- mrcal: Set up the camera model -----
    # mrcal needs intrinsics as a dictionary
    mrcal_intrinsics = (
        'LENSMODEL_OPENCV8',           # lens model string ("opencv5" is cv2's 5-param model)
        np.array([fx, fy, cx, cy, *dist_coeffs])
        # array: [fx, fy, cx, cy, k1, k2, p1, p2, k3]
    )

    # mrcal expects shape (N,2)
    pts_flat = pts.reshape(-1,2)

    # Undistort: mrcal.unproject_pinhole() gives (x,y) in normalized camera coords
    # but for accurate comparison, let's use mrcal.unsafe_unproject to get normalized rays
    # or mrcal.unproject to go normalized; then project using NO distortion (i.e. undistorted)
    v = mrcal.unproject(pts_flat, *mrcal_intrinsics)

    # Now project with distortion set to zero to simulate undistortion
    zero_dist_intrinsics = (
        'LENSMODEL_OPENCV5',
        np.array([fx, fy, cx, cy, 0,0,0,0,0], dtype=np.float64)
    )
    pts_undist_mrcal = mrcal.project(v, *zero_dist_intrinsics)
    print('\nmrcal undistorted (pixels):')
    print(pts_undist_mrcal)

    # Optionally, compare as difference
    print('\nDifference (OpenCV - mrcal, pixels):')
    print(pts_cv_undist.reshape(-1, 2) - pts_undist_mrcal)

if __name__ == '__main__':
    main()
