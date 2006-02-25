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
name|search
operator|.
name|Filter
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
name|RangeFilter
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
name|FilterBuilder
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
DECL|class|RangeFilterBuilder
specifier|public
class|class
name|RangeFilterBuilder
implements|implements
name|FilterBuilder
block|{
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|String
name|fieldName
init|=
name|DOMUtils
operator|.
name|getAttributeWithInheritance
argument_list|(
name|e
argument_list|,
literal|"fieldName"
argument_list|)
decl_stmt|;
name|String
name|lowerTerm
init|=
name|e
operator|.
name|getAttribute
argument_list|(
literal|"lowerTerm"
argument_list|)
decl_stmt|;
name|String
name|upperTerm
init|=
name|e
operator|.
name|getAttribute
argument_list|(
literal|"upperTerm"
argument_list|)
decl_stmt|;
name|boolean
name|includeLower
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"includeLower"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|includeUpper
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"includeUpper"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|RangeFilter
argument_list|(
name|fieldName
argument_list|,
name|lowerTerm
argument_list|,
name|upperTerm
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
block|}
end_class

end_unit

