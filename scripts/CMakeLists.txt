project(casadi_meme)

add_library(costs_4_tag SHARED casadi_meme.c)
add_executable(test_casadi main.cpp)
target_link_libraries(test_casadi PRIVATE costs_4_tag)
target_include_directories(test_casadi PRIVATE /usr/include/eigen3/)