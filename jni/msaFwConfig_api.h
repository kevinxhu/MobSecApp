/***************************************************
 * msaFirewallConfig_api.h
 * Mobile Security App Firewall Configuration public
 * definitions
 * Mario Mao
 ***************************************************/

#ifndef _INCLUDE_MSA_FW_CONFIG_API_H_
#define _INCLUDE_MSA_FW_CONFIG_API_H_

#include <linux/ioctl.h>

/* structure and definition */

/* The major device number. We can't rely on dynamic 
 * registration any more, because ioctls need to know 
 * it. */
#define MAJOR_NUM 100

/* Set the message of the device driver */
#define IOCTL_UPDATE_ACL_RULE	_IOR(MAJOR_NUM, 0, char *)
#define IOCTL_GET_TRAFFIC_STATS	_IOR(MAJOR_NUM, 1, int *)

/* The name of the device file */
#define DEVICE_FILE_NAME "msad_dev"


#endif /* _INCLUDE_MSA_FW_CONFIG_API_H_ */