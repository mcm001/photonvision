/*
 * MIT License
 *
 * Copyright (c) PhotonVision
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

#include <arpa/inet.h>
#include <sys/socket.h>

#include <thread>

#include <frc/Notifier.h>
#include <frc/RobotController.h>
#include <wpi/struct/Struct.h>

#include "jni.h"
// #include "org_photonvision_jni_TimeSyncJNI.h"

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
  // Check to ensure the JNI version is valid

  JNIEnv* env;
  if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)
    return JNI_ERR;

  // In here is also where you store things like class references
  // if they are ever needed

  return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {}

static int sock;
static struct sockaddr_in broadcast_addr;

static void do_ping(void) {
  fmt::println("Hello!");

  if (!sock) {
    fmt::println("sock invalid??");
  } else {
    fmt::println("Sending some test data...");

    uint64_t now = frc::RobotController::GetFPGATime();

    std::vector<uint8_t> data;
    data.resize(wpi::GetStructSize<decltype(now)>());
    wpi::PackStruct(data, now);

    int result = sendto(
        sock, reinterpret_cast<const char*>(data.data()), data.size(), 0,
        reinterpret_cast<sockaddr*>(&broadcast_addr), sizeof(broadcast_addr));
    if (result) {
      fmt::println("sendto: {}", result);
    }
  }
}

frc::Notifier notifier{do_ping};

/*
 * Class:     org_photonvision_jni_TimeSyncJNI
 * Method:    start
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_photonvision_jni_TimeSyncJNI_start
  (JNIEnv*, jclass)
{
  notifier.SetName("NTP At Home");
  notifier.Stop();

  int addr_len;
  const int PORT = 4445;
  int ret;

  sock = socket(AF_INET, SOCK_DGRAM, 0);
  if (sock < 0) {
    std::perror("sock error");
    return -1;
  }

  int yes = 1;
  ret = setsockopt(sock, SOL_SOCKET, SO_BROADCAST, (char*)&yes, sizeof(yes));
  if (ret == -1) {
    std::perror("setsockopt error");
    return 0;
  }

  addr_len = sizeof(struct sockaddr_in);

  std::memset((void*)&broadcast_addr, 0, addr_len);
  broadcast_addr.sin_family = AF_INET;
  // broadcast_addr.sin_addr.s_addr = htonl(INADDR_BROADCAST);
  inet_pton(AF_INET, "10.0.0.255", &(broadcast_addr.sin_addr));
  broadcast_addr.sin_port = htons(PORT);

  notifier.StartPeriodic(1_s);

  return 0;
}

/*
 * Class:     org_photonvision_jni_TimeSyncJNI
 * Method:    stop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_photonvision_jni_TimeSyncJNI_stop
  (JNIEnv*, jclass)
{
  notifier.Stop();
  close(sock);

  return 0;
}

}  // extern "C"
