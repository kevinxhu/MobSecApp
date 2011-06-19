/*
 * Mobile Security Application Firewall glue file
 * Mario Mao
 */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>		/* open */
#include <unistd.h>		/* exit */
#include <sys/ioctl.h>	/* ioctl */

#include "msaFwConfig_api.h"

void
Java_com_android_mobsec_ModSec_getRoot(JNIEnv* env, jobject thiz)
{
	system("su");

	return;
}

jint
Java_com_android_mobsec_policyList_updateFwAcl(JNIEnv* env, jobject thiz, jstring configPath)
{
	int fd, ret = 0, i, ch;
	FILE *fs;
	char buffer[8192];
	const jbyte *jp = (*env)->GetStringUTFChars(env, configPath, NULL);
	char *cfgFileName = (char *)jp;
	char devFileName[64];

	/* read configuration file */
	fs = fopen(cfgFileName, "r");
	if (fs == NULL) {
		return -1;
	}

	for (i = 0; i < 8192; i++)
	{
		ch = fgetc(fs);
		if (ch != EOF)
		{
			buffer[i] = (char)ch;
		}
		else if (feof(fs))
		{
			buffer[i] = '\0';
			break;
		}
	}

	fclose(fs);

	if(strlen(buffer) < 5)
	{
		buffer[0] = '\0';
	}

	/* open driver device to push configuration to MSA driver */
	sprintf(devFileName, "/dev/%s%d", DEVICE_FILE_NAME, 0);

	fd = open(devFileName, 0);
	if (fd < 0) {
		return -1;
	}

	system("/etc/init.d/nscd restart");

	ret = ioctl(fd, IOCTL_UPDATE_ACL_RULE, buffer);

	close(fd);

	return 0;
}

jint
Java_com_android_mobsec_policyStatus_getFwStats(JNIEnv* env, jobject thiz, jintArray stats)
{
	int rawStats[6];
	jint *fwStats = (*env)->GetIntArrayElements(env, stats, NULL);
	int fd, ret;
	char devFileName[64];

	/* open driver device to push configuration to MSA driver */
	sprintf(devFileName, "/dev/%s%d", DEVICE_FILE_NAME, 0);

	fd = open(devFileName, 0);
	if (fd < 0) {
		return -1;
	}

	ret = ioctl(fd, IOCTL_GET_TRAFFIC_STATS, rawStats);

	close(fd);

	if (ret == 0)
	{
		fwStats[0] = rawStats[0];	/* inPackets */
		fwStats[1] = rawStats[1];	/* inBytes */
		fwStats[2] = rawStats[2];	/* inDrops */
		fwStats[3] = rawStats[3];	/* outPackets */
		fwStats[4] = rawStats[4];	/* outBytes */
		fwStats[5] = rawStats[5];	/* outDrops */
	}

	return 0;
}

