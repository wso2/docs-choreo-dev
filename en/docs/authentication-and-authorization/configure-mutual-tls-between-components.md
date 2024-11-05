# Configure Mutual TLS Between Components

Mutual transport layer security (mutual TLS) is a protocol that ensures privacy, integrity, and authentication of the data transmitted between two endpoints. In mutual TLS, the client and the server authenticate each other using digital certificates, establishing trust and verifying identities. Upon successful authentication, mutual TLS encrypts the data exchanged between the client and the server, preventing unauthorized access.

In Choreo, you can use mutual TLS to establish secure connections between components within a project.

!!! note
     If mutual TLS is not required, you can configure TLS instead. TLS provides a secure communication channel between a client and server but does not require the client to present a certificate to the server. This results in the absence of mutual authentication between the client and the server. While TLS ensures the confidentiality of data transmitted between the client and server, preventing unauthorized tampering, mutual TLS enhances TLS by introducing client-side authentication and facilitating mutual verification of identities between the client and server. 

     To configure TLS, you can follow the same steps as for mutual TLS as mentioned below, *without having to generate a client certificate*. The client only needs the root certificate to verify the server's identity.

## Generate certificates to establish mutual TLS

- **Root certificate:** Trusted by both the client and the server, this certificate is used to verify the authenticity of other certificates presented during the mutual TLS handshake process and to issue certificates for clients and servers. For a specific project, you can generate a single root certificate using a tool like OpenSSL.

- **Client certificate:** Contains the client’s identity for authentication. The common name (CN) in the certificate identifies the client. The generated client certificate must be signed by the root certificate.

- **Server certificate:** Clients use the server certificate to verify the trustworthiness of the server and establish a secure and authenticated connection. Similar to the client certificates, the server certificate must also be signed by the root certificate. When generating the server certificate, you must specify the server's hostname for the subject alternative name (SAN). You can obtain the hostname for the specific version of a service component from any project endpoint on the **Overview** page.

For example, if your project endpoint is `http://my-service-3781140846:7080/todos`, the hostname will be `my-service-3781140846`.

## Read mutual TLS certificates from your component

The approach to read mutual TLS certificates from a component can vary depending on its implementation. Typically, a component can read the certificate data from the file system or via an environment variable. For detailed instructions on adding environment variables and file mounts to your application, see [Manage Configurations and Secrets](../devops-and-ci-cd/manage-configurations-and-secrets.md).

!!! info
    When you specify a **private key**, ensure you **save it as a secret**.

## Sample for mutual TLS communication

For a sample that demonstrates how you can deploy services that communicate using mutual TLS, see [service-to-service-mtls](https://github.com/wso2/choreo-samples/tree/main/docker-service-to-service-mtls).
