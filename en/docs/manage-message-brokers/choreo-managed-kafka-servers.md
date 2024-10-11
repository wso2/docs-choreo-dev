# Choreo-Managed Kafka Servers

Kafka on Choreo offers fully managed, distributed message broker services across AWS, Azure, GCP, and DigitalOcean. These services are designed to handle high-throughput, fault-tolerant data streaming use cases such as real-time analytics, event sourcing, and log aggregation.

## Create a Choreo managed Kafka service

Follow the steps below to create a Choreo-managed Kafka service:

1. Sign in to the Choreo Console at [https://console.choreo.dev/](https://console.choreo.dev).
2. In the header, click the **Organization** list. This opens the organization home page.
3. In the left navigation menu, click **Dependencies** and then click **Message Brokers**.
4. Click **+ Create** and provide a display name for the Kafka server and follow the instructions.
5. Select your preferred cloud provider from AWS, Azure, GCP, or Digital Ocean.
    - The cloud provider is used to provision the compute and storage infrastructure for your Kafka service.
    - There is no functional difference between Kafka services created on different cloud providers, apart from changes to service plans (and associated costs).
6. Choose the region for your Kafka service.
    - Available regions will depend on the selected cloud provider. Choreo currently supports US, EU, and AU regions across all providers.
7. Select the service plan.
    - Service plans vary in the dedicated CPU, memory (RAM), storage space allocated for your Kafka server, the backup retention periods, and high-availability configurations for production use cases.
8. Click **Create**.

## Connect to your Choreo-managed Kafka service

To connect to your Choreo-managed Kafka service, you will need the connection parameters, which are available on the **Service Overview** page. Choreo secures Kafka connections via client certificate authentication.

**Producer and Consumer Programs:** You can connect to the Kafka service using producer and consumer programs by configuring them with the provided credentials and connection parameters.

**Access Control:** By default, Kafka services accept traffic from the internet. However, for Kafka services, you can restrict access to specific IP addresses or CIDR blocks under Advanced Settings.

Below are sample code blocks written in Golang to help you configure producer and consumer programs. In the below examples, we assume the certificates are in the same folder with the code. If you keep the certificates in a dedicated place, please replace them with the full path. In order to produce and consume Kafka messages, you will first need to  [create a topic](./choreo-manage-kafka-topics.md). If you already had a topic created, go ahead and use the desired topic below.


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

