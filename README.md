# SBIBD_impl
A sample implementation scenario based on IEEE paper "Block Design-Based Key Agreement for Group Data Sharing in Cloud Computing".


## System Architecture 

This project roughly follows the system outline shown below.[^2]  
 
<img alt="system_arch.jpg" src="system_arch.jpg" width="403"/>

Essentially, "A TPA, cloud and users are involved in the model, where the TPA is responsible for cloud storage auditing, fault detection and generating the system parameters. The cloud, who is a semi-trusted party, provides users with data storage services and download services. Users can be individuals or staff in a company. To work together, they form a group, upload data to the cloud server and share the outsourced data with the group members. In practice, users can be mobile Android devices, mobile phones, laptops, nodes in underwater sensor networks and so forth."[^2]    

For the purpose of this project, users are a client application on some desktop device, which can send messages which are routed to through the cloud - where the information is exchanged and can be accessed by other users/clients. The symmetric balanced incomplete block design (SBIBD) key agreement protocol is used during the info exchange. The info exchange is valid only for the users in a specified group, and external entities including the cloud provider would not be able to participate or read the exchanged information.


[^1]: Shen, Jian, et al. “Block design-based key agreement for Group Data Sharing in cloud computing.” IEEE Transactions on Dependable and Secure Computing, vol. 16, no. 6, 1 Nov. 2019, pp. 1-15, https://doi.org/10.1109/tdsc.2017.2725953.  
[^2]: [^1] (Shen et al. 4)  