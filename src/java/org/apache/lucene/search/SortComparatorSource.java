begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Expert: returns a comparator for sorting ScoreDocs.  *  *<p>Created: Apr 21, 2004 3:49:28 PM  *   * @author  Tim Jones  * @version $Id$  * @since   1.4  */
end_comment

begin_interface
DECL|interface|SortComparatorSource
specifier|public
interface|interface
name|SortComparatorSource
extends|extends
name|Serializable
block|{
comment|/**    * Creates a comparator for the field in the given index.    * @param reader Index to create comparator for.    * @param fieldname  Field to create comparator for.    * @return Comparator of ScoreDoc objects.    * @throws IOException If an error occurs reading the index.    */
DECL|method|newComparator
name|ScoreDocLookupComparator
name|newComparator
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

