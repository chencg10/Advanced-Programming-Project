# Advanced-Programming-Project : Interactive Graph Visualization Tool

## Background
This project is designed to provide a dynamic and interactive way to visualize computational graphs. It leverages the power of D3.js for rendering graphical elements and Java for backend logic, including configuration management and topic message handling. The tool allows users to load configuration files, manipulate graph elements, and observe changes in real-time.

## Features
- **Dynamic Graph Visualization**: Utilizes D3.js to render nodes and edges, supporting interactive dragging and zooming.
- **Configuration Management**: Allows users to upload and apply configuration files that define the structure and properties of the graph.
- **Real-time Interaction**: Supports publishing messages to topics, with the graph updating to reflect these messages in real-time.

## Getting Started

### Prerequisites
- Java JDK 11 or higher
- A modern web browser with JavaScript enabled

### Running the Project

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/your-project-repo.git
   cd your-project-repo


## How to run it:
1. Compile the Java Code Navigate to the project directory and compile the Java code.
   <pre>javac -d bin src/**/*.java </pre>
2. Start the Server Run the MainTrain class to start the server.
   <pre>java -cp bin graph.MainTrain </pre>
3. Access the Application Open a web browser and navigate to http://localhost:8080/app/ to access the application interface.

## Commands
- Upload Configuration: Use the "Upload" button in the application interface to load a .conf file that defines the graph structure.
- Interact with the Graph: Click and drag nodes to reposition them. Use the interface to publish messages to topics and observe the graph's response.
- Deploy New Configuration: After modifying or uploading a new configuration file, use the "Deploy" button to apply changes to the graph.

## How It Works
- Backend: The Java backend handles HTTP requests, manages configurations, and processes topic messages. It dynamically updates the graph structure based on the configuration files and messages received.
- Frontend: The D3.js library renders the graph based on the data provided by the backend. It supports interactive manipulation of the graph elements.

## Enjoy! Chen&Avidan
