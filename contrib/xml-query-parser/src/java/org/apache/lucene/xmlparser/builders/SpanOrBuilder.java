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
name|ArrayList
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
name|SpanOrQuery
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
name|DOMUtils
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

begin_class
DECL|class|SpanOrBuilder
specifier|public
class|class
name|SpanOrBuilder
extends|extends
name|SpanBuilderBase
block|{
DECL|field|factory
name|SpanQueryBuilder
name|factory
decl_stmt|;
DECL|method|SpanOrBuilder
specifier|public
name|SpanOrBuilder
parameter_list|(
name|SpanQueryBuilder
name|factory
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
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
name|ArrayList
name|clausesList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|kid
init|=
name|e
operator|.
name|getFirstChild
argument_list|()
init|;
name|kid
operator|!=
literal|null
condition|;
name|kid
operator|=
name|kid
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
if|if
condition|(
name|kid
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|SpanQuery
name|clause
init|=
name|factory
operator|.
name|getSpanQuery
argument_list|(
operator|(
name|Element
operator|)
name|kid
argument_list|)
decl_stmt|;
name|clausesList
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
block|}
name|SpanQuery
index|[]
name|clauses
init|=
operator|(
name|SpanQuery
index|[]
operator|)
name|clausesList
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|clausesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|soq
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|clauses
argument_list|)
decl_stmt|;
name|soq
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|soq
return|;
block|}
block|}
end_class

end_unit

