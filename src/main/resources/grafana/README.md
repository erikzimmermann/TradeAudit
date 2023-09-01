# TradeAudit Grafana Dashboard Integration

This guide will walk you through the process of setting up the Grafana dashboard for TradeAudit, a Minecraft plugin that allows server admins to moderate, observe, and analyze trades between players.

## Prerequisites

Before you begin, make sure you have the following prerequisites in place:

- **TradeAudit Plugin:** Ensure that you have successfully installed and configured the TradeAudit plugin on your Minecraft server.

- **Grafana Installation:** Grafana should be installed and running on your server. You can download and install Grafana from the official website: [Grafana Installation](https://grafana.com/grafana/download?pg=get&plcmt=selfmanaged-box1-cta1).

- **MySQL Database:** Set up a MySQL database where TradeAudit can store trade-related data. Make sure you have the database credentials and connection details.

## Grafana Dashboard Setup

Follow these steps to set up the Grafana dashboard for TradeAudit:

1. **Import the Dashboard:**
    1. Log in to your Grafana instance using your web browser.
    2. Click on the menu icon in the upper-left corner.
    3. Under the "Dashboards" section, select "Import" at the "New" button. 
    4. In the "Import Dashboard" dialog, click on "Upload dashboard JSON file" and select the TradeAudit dashboard template from `plugins/TradeAudit/grafana`.
    5. Click on "Import".

2. **Configure MySQL Data Source:**
    1. Click on the menu icon in the upper-left corner.
    2. Go to "Connections" > "Add new connection".
    3. Select "MySQL" as the data source type.
    4. Click on "Add new data source".
    5. Configure the MySQL data source settings with the necessary connection details, including the host, port, database name, username, and password.
    6. Test the data source to ensure it's working correctly.
    7. Click on "Save".

3. **Configure the data source inside the dashboard:**
    1. Go back to your imported dashboard.
    2. Select the correct data source via the drop-down variable at the upper-left corner.
    3. Also make sure to set the correct database name next to the previous variable.

4. **Customize the Dashboard (Optional):**
    1. You can customize the dashboard panels, variables, and queries to suit your specific needs. Modify the dashboard as necessary to visualize the TradeAudit data effectively.

5. **Save and Access the Dashboard:**
    1. After importing and configuring the dashboard, make sure to save it.
    2. You can access the TradeAudit Grafana dashboard from the Grafana web interface under the "Dashboards" section.

## Usage

Once the TradeAudit Grafana dashboard is set up and connected to your MySQL database, you can use it to monitor and analyze trade activities on your Minecraft server efficiently. Customize the time range and filters to view specific trade data.

Also, if you want to monitor certain players, you can do so by using the built-in drop-down menus like you did with the data source.

## Support and Issues

If you encounter any issues or have questions regarding the TradeAudit Grafana dashboard integration, please feel free to [join our Discord server](https://discord.gg/9xbeuh2WJG). We'll be happy to assist you.

## License

This TradeAudit Grafana Dashboard is licensed under the [Minecraft Plugin License](../../../../LICENSE).

Happy monitoring and auditing!
