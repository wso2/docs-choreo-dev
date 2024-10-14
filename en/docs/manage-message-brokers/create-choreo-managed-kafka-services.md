# Create Choreo-Managed Kafka Services

Kafka on Choreo offers fully managed, distributed message broker services across AWS, Azure, GCP, and DigitalOcean. These services are designed to handle high-throughput, fault-tolerant data streaming use cases such as real-time analytics, event sourcing, and log aggregation.

## Create a Choreo-managed Kafka service

!!! note "Kafka service billing"
     Billing for Kafka services you create within your Choreo organization will be included in your current Choreo subscription. The pricing will vary depending on the service plan of the resources you create. For details, see [Choreo Platform Services Billing](../references/choreo-platform-services-billing.md).

Follow the steps below to create a Choreo-managed Kafka service:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. Click **+ Create**.
5. Specify a display name for the Kafka service and click **Next**.
6. Select your preferred cloud provider from AWS, Azure, GCP, or Digital Ocean.
    - The cloud provider is used to provision the computing and storage infrastructure for your Kafka service.
    - There is no functional difference between Kafka services created on different cloud providers, apart from changes to service plans and associated costs.
7. Select the region for your Kafka service.
    - Available regions depend on the selected cloud provider. Choreo currently supports US, EU, and AU regions across all providers.
8. Select the service plan.
    - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your Kafka server, the backup retention periods, and high-availability configurations for production use cases.
9. Click **Create**. This creates the Kafka service and takes you to the **Service Overview** page.

## Connect to your Choreo-managed Kafka service

To connect to your Choreo-managed Kafka service, use the connection parameters available on the **Service Overview** page. Choreo secures Kafka connections via client certificate authentication.

To connect the Kafka service using producer and consumer programs, you must configure them with the provided credentials and connection parameters.

By default, Kafka services accept traffic from the internet. However, if you want to restrict access to specific IP addresses or CIDR blocks, you can configure the necessary advanced settings.

The following are sample code blocks in [Go](https://go.dev/) to help you configure producer and consumer programs. The samples assume the certificates are in the same directory as the code. If you keep the certificates in a dedicated directory, you must specify the full path to the directory.

To produce and consume Kafka messages, you must first [create a topic](./configure-a-kafka-service.md#create-a-kafka-topic). If you already have a topic, you can use it in the sample.


=== "Producer"
    

            package main

            import (
                "context"
                "crypto/tls"
                "crypto/x509"
                "fmt"
                "io/ioutil"
                "log"
                "time"

                "github.com/segmentio/kafka-go"
            )

            func main() {
                TOPIC_NAME := "TOPIC_NAME"

                keypair, err := tls.LoadX509KeyPair("service.cert", "service.key")
                if err != nil {
                    log.Fatalf("Failed to load access key and/or access certificate: %s", err)
                }

                caCert, err := ioutil.ReadFile("ca.pem")
                if err != nil {
                    log.Fatalf("Failed to read CA certificate file: %s", err)
                }

                caCertPool := x509.NewCertPool()
                ok := caCertPool.AppendCertsFromPEM(caCert)
                if !ok {
                    log.Fatalf("Failed to parse CA certificate file: %s", err)
                }

                dialer := &kafka.Dialer{
                    Timeout:   10 * time.Second,
                    DualStack: true,
                    TLS: &tls.Config{
                        Certificates: []tls.Certificate{keypair},
                        RootCAs:      caCertPool,
                    },
                }

                // init producer
                producer := kafka.NewWriter(kafka.WriterConfig{
                    Brokers: []string{"kafka-48aafe1c-c389-4eb6-9285-2dd121f6fbce-check1717655554-chor.l.aivencloud.com:16364"},
                    Topic:   TOPIC_NAME,
                    Dialer:  dialer,
                })

                // produce 100 messages
                for i := 0; i < 100; i++ {
                    message := fmt.Sprint("Hello from Go using SSL ", i+1, "!")
                    producer.WriteMessages(context.Background(), kafka.Message{Value: []byte(message)})
                    log.Printf("Message sent: " + message)
                    time.Sleep(time.Second)
                }

                producer.Close()
            }

        

=== "Consumer"
        
    
            package main

            import (
                "context"
                "crypto/tls"
                "crypto/x509"
                "io/ioutil"
                "log"
                "time"

                "github.com/segmentio/kafka-go"
            )

            func main() {
                TOPIC_NAME := "TOPIC_NAME"

                keypair, err := tls.LoadX509KeyPair("service.cert", "service.key")
                if err != nil {
                    log.Fatalf("Failed to load access key and/or access certificate: %s", err)
                }

                caCert, err := ioutil.ReadFile("ca.pem")
                if err != nil {
                    log.Fatalf("Failed to read CA certificate file: %s", err)
                }

                caCertPool := x509.NewCertPool()
                ok := caCertPool.AppendCertsFromPEM(caCert)
                if !ok {
                    log.Fatalf("Failed to parse CA certificate file: %s", err)
                }

                dialer := &kafka.Dialer{
                    Timeout:   10 * time.Second,
                    DualStack: true,
                    TLS: &tls.Config{
                        Certificates: []tls.Certificate{keypair},
                        RootCAs:      caCertPool,
                    },
                }

                // init consumer
                consumer := kafka.NewReader(kafka.ReaderConfig{
                    Brokers: []string{"kafka-48aafe1c-c389-4eb6-9285-2dd121f6fbce-check1717655554-chor.l.aivencloud.com:16364"},
                    Topic:   TOPIC_NAME,
                    Dialer:  dialer,
                })

                for {
                    message, err := consumer.ReadMessage(context.Background())

                    if err != nil {
                        log.Printf("Could not read message: %s", err)
                    } else {
                        log.Printf("Got message using SSL: %s", message.Value)
                    }
                }
            }
