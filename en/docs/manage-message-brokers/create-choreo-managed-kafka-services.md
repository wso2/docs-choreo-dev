# Create Choreo-Managed Kafka Services

Kafka on Choreo offers fully managed, distributed message broker services across AWS, Azure, GCP, and DigitalOcean. These services are designed to handle high-throughput, fault-tolerant data streaming use cases such as real-time analytics, event sourcing, and log aggregation.

## Create a Choreo-managed Kafka service

Follow the steps below to create a Choreo-managed Kafka service:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. Click **+ Create**.
5. Specify a display name for the Kafka service and click **Next**.
6. Select your preferred cloud provider from AWS, Azure, GCP, or Digital Ocean.
    - The cloud provider provisions the computing and storage infrastructure for your Kafka service.
    - There is no functional difference between Kafka services across providers except for variations in service plans and associated costs.
7. Select a region for your Kafka service.
    - Available regions depend on the selected cloud provider. Choreo currently supports US, EU, and AU regions across all providers.
8. Select a service plan.
    - Service plans differ based on the allocation of CPU, memory (RAM), and storage for your Kafka server, as well as backup retention periods and high-availability options suited for production environments.
9. Click **Create**. This creates the Kafka service and takes you to the **Overview** tab on the service details page.

## Connect to your Choreo-managed Kafka service

To connect to your Choreo-managed Kafka service, use the connection parameters from the **Overview** tab on the service details page. Choreo secures Kafka connections via client certificate authentication.

![Kafka Service Overview ](../../assets/img/platform-services/message-broker-overview.png)

To use the Kafka service with producer and consumer programs, you must configure them with the provided credentials and connection parameters.

By default, Kafka services accept traffic from the internet. However, if you want to restrict access to specific IP addresses or CIDR blocks, you can configure the necessary advanced settings.

To use the Kafka service in producer and consumer programs, configure the required values as Configs and Secrets in Choreo. These values, available on the service overview page, include key configurations such as the access key, access certificate, CA certificate, and service URI. Before producing or consuming Kafka messages, make sure to [create a topic](./configure-a-kafka-service.md#create-a-kafka-topic). If a topic already exists, you can proceed.

### Setting Up Configs and Secrets

Begin by creating two Choreo components: one for the producer and another for the consumer program. Refer to the sample [Go](https://go.dev/) code below for implementing the producer and consumer. Then, define the required [Configs and Secrets](https://wso2.com/choreo/docs/devops-and-ci-cd/manage-configurations-and-secrets/) at the component level for each.

You can configure service.key, service.cert, and ca.pem using file mounts. The example below illustrates creating a file mount for the CA certificate; follow the same steps for the other files.

![Set CA Certificate](../../assets/img/platform-services/ca-cert.png)

Other configurations, such as TOPIC_NAME and SERVICE_URI, should be set as environment variables. For instance, you can define them as shown in the following example.

![Set Env Variables ](../../assets/img/platform-services/env-variables.png)

=== "Producer"
    

            package main

            import (
                "context"
                "crypto/tls"
                "crypto/x509"
                "fmt"
                "log"
                "os"
                "time"

                "github.com/segmentio/kafka-go"
            )

            func loadPEMFromFile(filePath string) ([]byte, error) {
                data, err := os.ReadFile(filePath)
                if err != nil {
                    return nil, fmt.Errorf("failed to read file %s: %w", filePath, err)
                }
                return data, nil
            }

            func main() {
                serviceCert, err := loadPEMFromFile("/service.cert")
                if err != nil {
                    log.Fatalf("Failed to load SERVICE_CERT: %s", err)
                }

                serviceKey, err := loadPEMFromFile("/service.key")
                if err != nil {
                    log.Fatalf("Failed to load SERVICE_KEY: %s", err)
                }

                caCert, err := loadPEMFromFile("/ca.pem")
                if err != nil {
                    log.Fatalf("Failed to load CA_CERT: %s", err)
                }

                keypair, err := tls.X509KeyPair(serviceCert, serviceKey)
                if err != nil {
                    log.Fatalf("Failed to load access key and/or access certificate: %s", err)
                }

                caCertPool := x509.NewCertPool()
                ok := caCertPool.AppendCertsFromPEM(caCert)
                if !ok {
                    log.Fatalf("Failed to parse CA certificate from environment variable")
                }

                dialer := &kafka.Dialer{
                    Timeout:   10 * time.Second,
                    DualStack: true,
                    TLS: &tls.Config{
                        Certificates: []tls.Certificate{keypair},
                        RootCAs:      caCertPool,
                    },
                }

                serviceURI := os.Getenv("SERVICE_URI")
                if serviceURI == "" {
                    fmt.Println("Environment variable 'SERVICE_URI' not set")
                }

                topicName := os.Getenv("TOPIC_NAME")
                if topicName == "" {
                    fmt.Println("Environment variable 'TOPIC_NAME' not set")
                }

                producer := kafka.NewWriter(kafka.WriterConfig{
                    Brokers: []string{serviceURI},
                    Topic:   topicName,
                    Dialer:  dialer,
                })

                for i := 0; i < 100; i++ {
                    message := fmt.Sprint("Hello from Go using SSL ", i+1, "!")
                    producer.WriteMessages(context.Background(), kafka.Message{Value: []byte(message)})
                    log.Printf("Message sent: " + message)
                    time.Sleep(time.Second)
                }

                defer producer.Close()
            }


        

=== "Consumer"
        
    
            package main

            import (
                "context"
                "crypto/tls"
                "crypto/x509"
                "fmt"
                "log"
                "os"
                "time"

                "github.com/segmentio/kafka-go"
            )

            func loadPEMFromFile(filePath string) ([]byte, error) {
                data, err := os.ReadFile(filePath)
                if err != nil {
                    return nil, fmt.Errorf("failed to read file %s: %w", filePath, err)
                }
                return data, nil
            }

            func main() {
                serviceCert, err := loadPEMFromFile("/service.cert")
                if err != nil {
                    log.Fatalf("Failed to load SERVICE_CERT: %s", err)
                }
                serviceKey, err := loadPEMFromFile("/service.key")
                if err != nil {
                    log.Fatalf("Failed to load SERVICE_KEY: %s", err)
                }
                caCert, err := loadPEMFromFile("/ca.pem")
                if err != nil {
                    log.Fatalf("Failed to load CA_CERT: %s", err)
                }

                keypair, err := tls.X509KeyPair(serviceCert, serviceKey)
                if err != nil {
                    log.Fatalf("Failed to load access key and/or access certificate: %s", err)
                }

                caCertPool := x509.NewCertPool()
                ok := caCertPool.AppendCertsFromPEM(caCert)
                if !ok {
                    log.Fatalf("Failed to parse CA certificate from environment variable")
                }

                dialer := &kafka.Dialer{
                    Timeout:   10 * time.Second,
                    DualStack: true,
                    TLS: &tls.Config{
                        Certificates: []tls.Certificate{keypair},
                        RootCAs:      caCertPool,
                    },
                }

                serviceURI := os.Getenv("SERVICE_URI")
                if serviceURI == "" {
                    fmt.Println("Environment variable 'SERVICE_URI' not set")
                }
                topicName := os.Getenv("TOPIC_NAME")
                if topicName == "" {
                    fmt.Println("Environment variable 'TOPIC_NAME' not set")
                }

                consumer := kafka.NewReader(kafka.ReaderConfig{
                    Brokers: []string{serviceURI},
                    Topic:   topicName,
                    Dialer:  dialer,
                })
                defer consumer.Close()

                for {
                    message, err := consumer.ReadMessage(context.Background())
                    if err != nil {
                        log.Printf("Could not read message: %s", err)
                    } else {
                        log.Printf("Got message using SSL: %s", message.Value)
                    }
                }
            }

