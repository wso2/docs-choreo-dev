# Data Mapping

A data mapping statement can be used to create variables by visually mapping one or more variables. The most common use of data 
mapping is to map the data from a response of an API call to a JSON. 

1. In the **Statements window**, select **Data Mapping**.
   
2. You can see the **Input** and **Output** windows as follows.

     ![Data mapping input output windows](../assets/img/references/datamapping/input-output-windows.png){.cInlineImage-full}
   
3. To select the variable to use as the input, click **Add Variable** in the **Input** window on the left-hand side 
   and select the input type from the drop-down menu.

4. To select the variable to use as the output, select an existing variable or create a new variable in the **Output** 
   window on the right-hand side. For the JSON output types, you can specify a sample JSON like 
   `{country: "", cases:0}` instead of `{}` if needed. 

5. After you have selected both sides the connectors in the middle will appear.

     ![Data mapping connections](../assets/img/references/datamapping/data-mapping-connections.png){.cInlineImage-full}
   
    The **Input** window shows constant values (e.g. `0`) and the input variables.

6. For the outputs of type `JSON`, you can add new fields to the output by selecting **Add value field** or 
   **Add complex field** icons next to the variable name.

7. You can map data by connecting the terminals from the **Input** window to the terminals in the middle connectors. 

8. You can edit the expressions by clicking on the **<>** icon in the middle connectors.

     ![Data mapping update expressions](../assets/img/references/datamapping/update-expressions.png){.cInlineImage-full}