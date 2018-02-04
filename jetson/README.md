## Rules of Thumb
The following instructions are somewhat complex and assume some Linux and Jetson familiarity.
They are *general guidelines* and not all the details are listed in any particular steps.
There are harmful consequences if some of these steps are done incorrectly, including overwriting good work or damaging the operating system.
Thus, if you don't understand something or don't know how to procede, **ask for help** from someone else or a mentor!

## Operating System Setup
- A Jetson [TX1](https://elinux.org/Jetson_TX1) or [TX2](https://elinux.org/Jetson_TX2) must fully power on when the power button is pressed.
- It must be flashed with the Nvidia's [Jetpack SDK](https://developer.nvidia.com/embedded/jetpack), 3.1 for the TX1 or 3.2 for the TX2, to install the base L4T operating system and associated drivers.
- **If using the** [**Orbitty Carrier Board**](connecttech.com/product/orbitty-carrier-for-nvidia-jetson-tx2-tx1/), the Jetson must be flashed additionally with CTI's [L4T support package](connecttech.com/product/orbitty-carrier-for-nvidia-jetson-tx2-tx1/) to install their specific packages.
- A user account named `roboloco` must set up on the system using [*adduser*](https://manpages.ubuntu.com/manpages/xenial/en/man8/adduser.8.html).
- The account `roboloco` must be a member of sudo and video groups using [*usermod*](https://manpages.ubuntu.com/manpages/xenial/man8/usermod.8.html).
- Passwords for `root`, `nvidia`, `ubuntu`, and `roboloco` must be set using [*passwd*](https://manpages.ubuntu.com/manpages/trusty/man5/passwd.5.html) based on this [link](https://goo.gl/G4gK9V).
- Change the hostname to something unique for the device. For example `tx1` and `tx2` have been used for the two existing Jetson modules. 
  - Change the active and boot hostname using `nmcli g hostname <hostname>`. For example `nmcli g hostname tx2`. 
  - Update `/etc/hosts` so `127.0.1.1` uses the new hostname. 
    - Edit `/etc/hosts`
    - Change `tegra-ubuntu` to the new hostname. If the hostname was previously changed the old hostname will be there instead. 
    - For example: 
      ```
      127.0.0.1 localhost
      127.0.1.1 tegra-ubuntu
      ```
      Should be changed to
      ```
      127.0.0.1 localhost
      127.0.1.1 tx2
      ```
- Wired Connection 1 - Edit via [*nmtui*](https://manpages.ubuntu.com/manpages/xenial/en/man1/nmtui.1.html)
  - IP must be changed to be a static `10.53.38.75/24` for only the wired `eth0` connection.
  - The checkbox for `Never use this network for default route` must be checked fro the Wired Connection. 
  - Do NOT set a default gateway for the wired connection. It must be blank or traffic for the internet will try to use the Robot network to get out. 
  - Do not set any DNS servers for the interface. 
  - Set IPv6 to ignore. 
  - The checkbox `Automatically connect` MUST be checked. The checkbox `Available to all users` may be checked. 
- (TX1 ONLY) All home directories should be set up on the external SD card using [these commands](https://help.ubuntu.com/community/Partitioning/Home/Moving).
- (TX2 ONLY) Enable the two disabled cores by editing `/etc/nvpmodel.conf` and changing the `< PM_CONFIG DEFAULT >` line to to `< PM_CONFIG DEFAULT=0 >` using, as sudo, [*vi*](https://manpages.ubuntu.com/manpages/xenial/en/man1/vi.1posix.html).
<!--- Check if above statement is actually true and edit to actually match the line number and provide entire line--->
- The `tegrastats` command should be copied from the `nvidia` home directory to `/usr/bin` using, as sudo, [*cp*](https://manpages.ubuntu.com/manpages/xenial/man1/cp.1.html).
- The `wifi-Gp.sh` and `wifi-lcps.sh` scripts should be moved into `/root` using, as sudo, [*mv*](https://manpages.ubuntu.com/manpages/xenial/man1/mv.1.html).
- The `roboloco.service` script should be moved into `/lib/systemd/system` using, as sudo, [*mv*](https://manpages.ubuntu.com/manpages/xenial/man1/mv.1.html).
- Activate systemd changes using systemctl daemon-reload
- Enable service on boot using systemctl enable roboloco
- Code directory with active subfolder should be in roboloco home directory

## Software Dependencies
Following commands came partly from this [site](https://jkjung-avt.github.io/opencv3-on-tx2/)
- python3.5, python3-pip, python3-dev, and python3-tk must be installed using apt
- tmux, screen, atop, htop, and vim should be installed using apt
- pynetworktables, matplotlib, and numpy should be installed using sudo and pip3

Run the following to build openCV for python3.
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
```
Change line 41 to `backend      : TkAgg` when running the following command
```
sudo vim /usr/local/lib/python3.5/dist-packages/matplotlib/mpl-data/matplotlibrc and 
```
Comment out line 62-66 as well as 68 when running the following command
```
sudo vim /usr/local/cuda-8.0/include/cuda_gl_interop.h 
```
Run these commands
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
Change the 6.2 to 5.3 for the TX1 only, then run this command.
```
cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local \
        -D WITH_CUDA=ON -D CUDA_ARCH_BIN="6.2" -D CUDA_ARCH_PTX="" \
        -D WITH_CUBLAS=ON -D ENABLE_FAST_MATH=ON -D CUDA_FAST_MATH=ON \
        -D ENABLE_NEON=ON -D WITH_LIBV4L=ON -D BUILD_TESTS=OFF \
        -D BUILD_PERF_TESTS=OFF -D BUILD_EXAMPLES=OFF \
        -D WITH_QT=ON -D WITH_OPENGL=ON ..
```

Change the -j4 to -j6 for the TX2 only, then run these commands.
```
make -j4
sudo make install
```

## SSH Key Based Authentication
### OSX & Linux
#### If you don't have an existing SSH key
```
ssh-keygen -t ed25519
```
- When asked for a password you can either enter a password to encrypt the key with or just press enter to not encrypt them. 
- If you use a password you'll be asked to enter it every time the key is loaded. For instance when you add it to the SSH Agent, it'll prompt you for the password. You can use it via the agent as long as the agent runs without prompting. 

#### Copy the ssh key to the Jetson
```
ssh-copy-id roboloco@<jetson ip>
```

#### Start the agent
1) `ssh-agent`
1) `ssh-add`

### Windows
#### If you don't already have an existing SSH key
1) Install (PuTTY)[https://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html]
1) Open `PuTTY Key Generator` (aka `PuTTY Gen`)
1) Set the key type to `SSH2-RSA`
1) Set the key size (aka number of bits) to `4096`. You can use the default of `2048` but this key will become insecure pretty quickly. 
1) Click `Generate`
1) Click `Save Privatae Key` and save the key somewhere save like your `My Documents`. Do not every give out this key. 
1) Click `Save Public Key` and save it with the private key. This key doens't need to be protected. 

#### Copy the ssh key to the Jetson
1) Copy the contents of the public key file - Use wordpad or (Notepad++)[https://notepad-plus-plus.org/] to view the file contents. Notepad might not work. 
  1) Place the contents of the file in `/home/roboloco/.ssh/authorized_keys` on the Jetson

## Finished
At this point, the system should be set up and all software dependencies are installed. You can push code to the Jetson of choice/setup using the existing build scripts in the main project.
