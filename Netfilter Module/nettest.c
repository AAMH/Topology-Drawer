#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/netfilter.h>
#include <linux/netfilter_ipv4.h>
#include <linux/ip.h>
#include <linux/tcp.h>
#include <linux/inet.h>

/*
#include <linux/netpoll.h>
#define MESSAGE_SIZE 1024
#define INADDR_LOCAL ((unsigned long int)0xac100180) //172.16.1.128
#define INADDR_SEND ((unsigned long int)0xac100101) //172.16.1.1
static struct netpoll* np = NULL;
static struct netpoll np_t;
*/

static struct nf_hook_ops nfho;         //struct holding set of hook function options
static struct nf_hook_ops nfho_out; 
int i;
int j;
//function to be called by hook
unsigned int hook_func(unsigned int hooknum, struct sk_buff *skb, const struct net_device *in, const struct net_device *out, int (*okfn)(struct sk_buff *))
{
  struct iphdr *ip_header = (struct iphdr *)skb_network_header(skb); //you can access to IP source and dest - ip_header->saddr, ip_header->daddr
    
//  if ((ip_header->saddr < in_aton("172.17.255.255")) && ( ip_header->saddr > in_aton("172.17.0.0")) ) 
//  {
    printk(KERN_ALERT "Customized Module Triggerred (IN): ");
    printk(KERN_ALERT "Protocol: %u\n", ip_header->protocol);
    printk(KERN_ALERT "Source IP: %u\n", ip_header->saddr);
    printk(KERN_ALERT "Dest IP: %u\n", ip_header->daddr);
    i+=1;
    printk(KERN_ALERT "Income Packet Amount %u\n", i);
//  }
  //return NF_DROP;
  return NF_ACCEPT;  //accept the packet
}

unsigned int hook_func_out(unsigned int hooknum, struct sk_buff *skb, const struct net_device *in, const struct net_device *out, int (*okfn)(struct sk_buff *))
{
  struct iphdr *ip_header = (struct iphdr *)skb_network_header(skb);
  
// if ((ip_header->saddr < in_aton("172.17.255.255")) && (ip_header->saddr > in_aton("172.17.0.0")) ) 
//  {
    printk(KERN_ALERT "Customized Module Triggerred (OUT): ");
    printk(KERN_ALERT "Protocol: %u\n", ip_header->protocol);
    printk(KERN_ALERT "Source IP: %u%pi4\n", ip_header->saddr);
    printk(KERN_ALERT "Dest IP: %u%pi4\n", ip_header->daddr);
    j+=1;
    printk(KERN_ALERT "Outcome Packet Amount %u\n", j);
//}
  return NF_ACCEPT; 
}


//Called when module loaded using 'insmod'
int init_module()
{
printk(KERN_ALERT "sdfsadfsadfas\n");

  nfho.hook = hook_func;                       //function to call when conditions below met
  //nfho.hooknum = NF_INET_PRE_ROUTING;        //called right after packet recieved, first hook in Netfilter
  nfho.hooknum = NF_INET_LOCAL_IN;
  nfho.pf = PF_INET;                           //IPV4 packets
  nfho.priority = NF_IP_PRI_FIRST;             //set to highest priority over all other hook functions
  nf_register_hook(&nfho);                     //register hook

  nfho_out.hook = hook_func_out;
  nfho_out.hooknum = NF_INET_LOCAL_OUT;
  nfho_out.pf = PF_INET;                           
  nfho_out.priority = NF_IP_PRI_FIRST;             
  nf_register_hook(&nfho_out);


/*
np_t.name = "LRNG";
strlcpy(np_t.dev_name, "eth0", IFNAMSIZ);
np_t.local_ip.ip = htonl(INADDR_LOCAL);
np_t.remote_ip.ip = htonl(INADDR_SEND);
np_t.local_port = 6665;
np_t.remote_port = 8901;
memset(np_t.remote_mac, 0xff, ETH_ALEN);
netpoll_print_options(&np_t);
netpoll_setup(&np_t);
np = &np_t;

char message[MESSAGE_SIZE];
sprintf(message,"%d\n",42);
int len = strlen(message);
netpoll_send_udp(np,message,len);
*/
  return 0;                                    //return 0 for success
}

//Called when module unloaded using 'rmmod'
void cleanup_module()
{
  nf_unregister_hook(&nfho);                   //cleanup – unregister hook
  nf_unregister_hook(&nfho_out); 
}

MODULE_LICENSE("GPL");

