begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucenesandbox.xmlindexingdemo
package|package
name|org
operator|.
name|apache
operator|.
name|lucenesandbox
operator|.
name|xmlindexingdemo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|XMLDocumentHandlerDOM
specifier|public
class|class
name|XMLDocumentHandlerDOM
block|{
DECL|method|createXMLDocument
specifier|public
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|createXMLDocument
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|document
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
argument_list|()
decl_stmt|;
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|DocumentBuilder
name|df
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|d
init|=
name|df
operator|.
name|parse
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Node
name|root
init|=
name|d
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|traverseTree
argument_list|(
name|root
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"error: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|document
return|;
block|}
DECL|method|traverseTree
specifier|static
specifier|private
name|void
name|traverseTree
parameter_list|(
name|Node
name|node
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
name|document
parameter_list|)
block|{
name|NodeList
name|nl
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
name|Node
name|parentNode
init|=
name|node
operator|.
name|getParentNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentNode
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
comment|//		    String parentNodeName = parentNode.getNodeName();
comment|// 		    String nodeValue = node.getNodeValue();
comment|// 		    if (parentNodeName.equals("name"))
comment|// 		    {
name|Node
name|siblingNode
init|=
name|node
operator|.
name|getNextSibling
argument_list|()
decl_stmt|;
if|if
condition|(
name|siblingNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|siblingNode
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"name"
argument_list|,
name|siblingNode
operator|.
name|getNodeValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 		    }
comment|// 		    else if (parentNodeName.equals("profession"))
comment|// 		    {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if (siblingNode != null)
comment|//                         {
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|//                             {
comment|// 				document.add(Field.Text([arentNodeName, siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
comment|// 		    else if (parentNodeName == "addressLine1")
comment|//                     {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if(siblingNode != null)
comment|// 			{
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|// 		            {
comment|// 				document.add(Field.Text("addressLine1", siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
comment|// 		    else if (parentNodeName.equals("addressLine2"))
comment|// 		    {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if (siblingNode != null)
comment|// 			{
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|// 			    {
comment|// 				document.add(Field.Text("addressLine2", siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
comment|// 		    if (parentNodeName.equals("city"))
comment|// 		    {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if (siblingNode != null)
comment|//                         {
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|// 			    {
comment|// 				document.add(Field.Text("city", siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
comment|// 		    else if (parentNodeName.equals("zip"))
comment|// 		    {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if (siblingNode != null)
comment|// 			{
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|// 			    {
comment|// 				document.add(Field.Text("zip", siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
comment|// 		    else if (parentNodeName.equals("state"))
comment|// 		    {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if (siblingNode != null)
comment|// 			{
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|// 			    {
comment|// 				document.add(Field.Text("state", siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
comment|// 		    else if (parentNodeName.equals("country"))
comment|// 		    {
comment|// 			Node siblingNode = node.getNextSibling();
comment|// 			if (siblingNode != null)
comment|// 			{
comment|// 			    if (siblingNode.getNodeType() == Node.CDATA_SECTION_NODE)
comment|// 			    {
comment|// 				document.add(Field.Text("country", siblingNode.getNodeValue()));
comment|// 			    }
comment|// 			}
comment|// 		    }
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|traverseTree
argument_list|(
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
name|document
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

