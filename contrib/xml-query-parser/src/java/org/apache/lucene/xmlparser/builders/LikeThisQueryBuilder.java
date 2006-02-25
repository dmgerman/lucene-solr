begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 25-Jan-2006  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|similar
operator|.
name|MoreLikeThisQuery
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
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|QueryBuilder
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
DECL|class|LikeThisQueryBuilder
specifier|public
class|class
name|LikeThisQueryBuilder
implements|implements
name|QueryBuilder
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|defaultFieldNames
name|String
name|defaultFieldNames
index|[]
decl_stmt|;
DECL|field|defaultMaxQueryTerms
name|int
name|defaultMaxQueryTerms
init|=
literal|20
decl_stmt|;
DECL|field|defaultMinTermFrequency
name|int
name|defaultMinTermFrequency
init|=
literal|1
decl_stmt|;
DECL|field|defaultPercentTermsToMatch
name|float
name|defaultPercentTermsToMatch
init|=
literal|30
decl_stmt|;
comment|//default is a 3rd of selected terms must match
DECL|method|LikeThisQueryBuilder
specifier|public
name|LikeThisQueryBuilder
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
index|[]
name|defaultFieldNames
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|this
operator|.
name|defaultFieldNames
operator|=
name|defaultFieldNames
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.lucene.xmlparser.QueryObjectBuilder#process(org.w3c.dom.Element) 	 */
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
name|String
name|fieldsList
init|=
name|e
operator|.
name|getAttribute
argument_list|(
literal|"fieldNames"
argument_list|)
decl_stmt|;
comment|//a comma-delimited list of fields
name|String
name|fields
index|[]
init|=
name|defaultFieldNames
decl_stmt|;
if|if
condition|(
operator|(
name|fieldsList
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|fieldsList
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|fields
operator|=
name|fieldsList
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
comment|//trim the fieldnames
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fields
index|[
name|i
index|]
operator|=
name|fields
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
block|}
name|MoreLikeThisQuery
name|mlt
init|=
operator|new
name|MoreLikeThisQuery
argument_list|(
name|DOMUtils
operator|.
name|getText
argument_list|(
name|e
argument_list|)
argument_list|,
name|fields
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|setMaxQueryTerms
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"maxQueryTerms"
argument_list|,
name|defaultMaxQueryTerms
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setMinTermFrequency
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"minTermFrequency"
argument_list|,
name|defaultMinTermFrequency
argument_list|)
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|setPercentTermsToMatch
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"percentTermsToMatch"
argument_list|,
name|defaultPercentTermsToMatch
argument_list|)
operator|/
literal|100
argument_list|)
expr_stmt|;
name|mlt
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
name|mlt
return|;
block|}
block|}
end_class

end_unit

