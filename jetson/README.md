## Operating System Setup
- A Jetson TX1 or TX2 that fully powers on.
- It flashed with the [latest L4T OS](https://developer.nvidia.com/embedded/jetpack), 3.1 or higher, and associated drivers from Nvidia based on their instructions.
- **If** using the [Orbitty Carrier Board](connecttech.com/product/orbitty-carrier-for-nvidia-jetson-tx2-tx1/), follow the instructions in their [L4T support package](connecttech.com/product/orbitty-carrier-for-nvidia-jetson-tx2-tx1/) to install the specific packages needed.
- A user account named roboloco set up on the system using [*adduser*](manpages.ubuntu.com/manpages/xenial/en/man8/adduser.8.html).
- roboloco has access as a member of sudo and video groups using [*usermod*](manpages.ubuntu.com/manpages/xenial/man8/usermod.8.html).
- Passwords for root, nvidia, ubuntu, and roboloco set using [*passwd*](manpages.ubuntu.com/manpages/trusty/man5/passwd.5.html) based on this [link](https://goo.gl/G4gK9V).
- IP changed to be a static 10.53.38.75/24 for the wired eth0 connection only using [*nmtui*](manpages.ubuntu.com/manpages/xenial/en/man1/nmtui.1.html).

- SD card as a home directory for TX1, ext4 formatted, or using onboard storage on TX2.
- roboloco.service moved to /lib/systemd/system
- Activate systemd changes using systemctl daemon-reload
- Enable service on boot using systemctl enable roboloco
- wifi-Gp.sh and wifi-lcps.sh moved to /root
- tegrastats moved to /usr/bin
- Code directory with active subfolder should be in roboloco home directory
- On TX2, fix two cores being disabled by editing /etc/nvpmodel.conf and changing the default state to 2

## Software Dependencies
Following commands come from this [site](https://jkjung-avt.github.io/opencv3-on-tx2/)
- python3.5, python3-pip, python3-dev, and python3-tk must be installed using apt
- tmux, screen, atop, htop, and vim should be installed using apt
- pynetworktables, matplotlib, and numpy should be installed using sudo and pip3

Run the following only if openCV has not been built yet.
```
sudo apt-get purge libopencv4tegra-python libopencv4tegra-dev \
                     libopencv4tegra
sudo apt-get purge libopencv4tegra-repo
sudo apt-get purge python-numpy
sudo apt autoremove
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install build-essential make cmake cmake-curses-gui \
                       g++ libavformat-dev libavutil-dev \
                       libswscale-dev libv4l-dev libeigen3-dev \
                       libglew-dev libgtk2.0-dev
sudo apt-get install libdc1394-22-dev libxine2-dev \
                       libgstreamer1.0-dev \
                       libgstreamer-plugins-base1.0-dev
sudo apt-get install libjpeg8-dev libjpeg-turbo8-dev libtiff5-dev \
                       libjasper-dev libpng12-dev libavcodec-dev
sudo apt-get install libxvidcore-dev libx264-dev libgtk-3-dev \
                       libatlas-base-dev gfortran
sudo apt-get install qt5-default
sudo vim /usr/local/lib/python3.5/dist-packages/matplotlib/mpl-data/matplotlibrc and change line 41 to 'backend      : TkAgg'
```

Comment out line 62-66 as well as 68 when running the following command
```
sudo vim /usr/local/cuda-8.0/include/cuda_gl_interop.h 
```
```
cd /usr/lib/aarch64-linux-gnu/
sudo ln -sf tegra/libGL.so libGL.so
mkdir -p ~/src
cd ~/src
wget https://github.com/opencv/opencv/archive/3.4.0.zip \
       -O opencv-3.4.0.zip
unzip opencv-3.4.0.zip
cd ~/src/opencv-3.4.0
mkdir build
cd build
```

Change the 6.2 to 5.3 for the TX1, run as below for the TX2.
```
cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local \
        -D WITH_CUDA=ON -D CUDA_ARCH_BIN="6.2" -D CUDA_ARCH_PTX="" \
        -D WITH_CUBLAS=ON -D ENABLE_FAST_MATH=ON -D CUDA_FAST_MATH=ON \
        -D ENABLE_NEON=ON -D WITH_LIBV4L=ON -D BUILD_TESTS=OFF \
        -D BUILD_PERF_TESTS=OFF -D BUILD_EXAMPLES=OFF \
        -D WITH_QT=ON -D WITH_OPENGL=ON ..
```

Change the -j4 to -j6 for the TX2 only, run as below for the TX1.
```
make -j4
```
```
sudo make install
```

## All done
At this point, you can push code to the jetson of choice/setup using the existing build scripts in the main project.