# Configuring mutual TLS between Components

## What is mTLS?

Mutual Transport Layer Security (mTLS) is a Transport Layer Security (TLS) protocol that ensures the privacy, integrity, and authentication of data transmitted between two endpoints. In mTLS, both the client and the server authenticate each other using digital certificates. This mutual authentication helps establish trust and ensures that both parties are who they claim to be. Once authentication is successful, mTLS encrypts the data exchanged between the client and the server, protecting it from unauthorized access.

## Configuring mTLS between Components

In Choreo, you can utilize mTLS to establish secure connections between components within a Choreo Project.

### Certificate Generation

- **Root Certificate:** The root certificate is the certificate trusted by both the client and the server. It is used to verify the authenticity of other certificates presented during the mTLS handshake process and issue certificates for clients and servers. For a specific project, you can generate a single root certificate using a tool like OpenSSL.

- **Client Certificate:** The client certificate contains the client's identity, which can be used to authenticate the client. You can use the Common Name (CN) in the certificate to provide an identifier for the client. The generated certificate should be signed by the previously generated root certificate.

- **Server Certificate:** The server certificate allows clients to ensure that they are connecting to a trusted server and establish a secure and authenticated connection. Similar to client certificates, the server certificate should be signed by the previously generated root certificate. When generating the server certificate, you need to provide the server's hostname for the Subject Alternative Name (SAN). You can extract the hostname for the specific version of a service component from any project endpoint in the Overview page.

For example, if your project endpoint is `http://my-service-3781140846:7080/todos`, the hostname will be `my-service-3781140846`.

### Reading mTLS Certificates from Your Component

The method for providing mTLS certificates to a component may vary depending on how the component is implemented. Typically, a component can read the certificate data from either the file system or environment variables. For detailed instructions on adding environment variables and file mounts to your application, please refer to the [Manage Configurations and Secrets](../devops-and-ci-cd/manage-configurations-and-secrets.md). When providing the private key, ensure that you save it as a Secret.

## Sample for mTLS Communication

The sample [service-to-service-mtls](https://github.com/wso2/choreo-sample-apps/tree/main/go/service-to-service-mtls) provides step-by-step instructions on how to deploy two components in Choreo that use mTLS communication.


