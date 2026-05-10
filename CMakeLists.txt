cmake_minimum_required(VERSION 3.22.1)

project("vcore")

add_library(
    vphone_core
    SHARED
    src/main/cpp/vphone_core.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    vphone_core
    ${log-lib}
)
