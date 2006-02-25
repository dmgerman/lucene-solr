begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|builders
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|xmlparser
operator|.
name|ParserException
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
name|Element
import|;
end_import

begin_comment
comment|/**  * @author maharwood  */
end_comment

begin_class
DECL|class|SpanQueryBuilderFactory
specifier|public
class|class
name|SpanQueryBuilderFactory
implements|implements
name|SpanQueryBuilder
block|{
DECL|field|builders
name|HashMap
name|builders
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
return|return
name|getSpanQuery
argument_list|(
name|e
argument_list|)
return|;
block|}
DECL|method|addBuilder
specifier|public
name|void
name|addBuilder
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|SpanQueryBuilder
name|builder
parameter_list|)
block|{
name|builders
operator|.
name|put
argument_list|(
name|nodeName
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|SpanQueryBuilder
name|builder
init|=
operator|(
name|SpanQueryBuilder
operator|)
name|builders
operator|.
name|get
argument_list|(
name|e
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParserException
argument_list|(
literal|"No SpanQueryObjectBuilder defined for node "
operator|+
name|e
operator|.
name|getNodeName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|builder
operator|.
name|getSpanQuery
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
end_class

end_unit

