# Configure Mutual TLS Between Components

The Mutual Transport Layer Security (mTLS) is a Transport Layer Security (TLS) protocol that ensures privacy, integrity, and authentication of the data transmitted between two endpoints. In mTLS, both the client and the server authenticate each other using digital certificates, establishing trust and confirming their identities. Upon successful authentication, mTLS encrypts the data exchanged between the client and the server, preventing unauthorized access.

## Configuring mTLS between Components

In Choreo, you can use mTLS to establish secure connections between components within a Choreo Project.

### Certificate Generation

- **Root Certificate:** The root certificate is trusted by both the client and the server. It is used to verify the authenticity of other certificates presented during the mTLS handshake process and issue certificates for clients and servers. For a specific project, you can generate a single root certificate using a tool like OpenSSL.

- **Client Certificate:** The client certificate contains the client's identity, which is used to authenticate the client. You can use the Common Name (CN) in the certificate to provide an identifier for the client. The generated certificate should be signed by the previously generated root certificate.

- **Server Certificate:** Clients can use the server certificate to verify the trustworthiness of the server and establish a secure and authenticated connection. Like client certificates, the server certificate must be signed by the previously generated root certificate. When generating the server certificate, you must specify the server's hostname for the Subject Alternative Name (SAN). You can obtain the hostname for the specific version of a service component from any project endpoint on the **Overview** page.

For example, if your project endpoint is `http://my-service-3781140846:7080/todos`, the hostname will be `my-service-3781140846`.

### Reading mTLS Certificates from your component

The method of reading mTLS certificates from a component may vary depending on its implementation. Typically, a component can read the certificate data from either the file system or via an environment variable. For detailed instructions on adding environment variables and file mounts to your application, refer to [Manage Configurations and Secrets](../devops-and-ci-cd/manage-configurations-and-secrets.md).

!!! note
    When providing the **private key**, ensure that you **save it as a Secret**.

## Sample for mTLS Communication

You can find step-by-step instructions on deploying two components in Choreo that utilize mTLS communication in the sample [service-to-service-mtls](https://github.com/wso2/choreo-sample-apps/tree/main/go/service-to-service-mtls).
