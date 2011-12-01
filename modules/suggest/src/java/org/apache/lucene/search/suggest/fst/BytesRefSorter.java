begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
package|;
end_package

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
name|util
operator|.
name|Iterator
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Collects {@link BytesRef} and then allows one to iterate over their sorted order. Implementations  * of this interface will be called in a single-threaded scenario.    */
end_comment

begin_interface
DECL|interface|BytesRefSorter
specifier|public
interface|interface
name|BytesRefSorter
block|{
comment|/**    * Adds a single suggestion entry (possibly compound with its bucket).    *     * @throws IOException If an I/O exception occurs.    * @throws IllegalStateException If an addition attempt is performed after    * a call to {@link #iterator()} has been made.    */
DECL|method|add
name|void
name|add
parameter_list|(
name|BytesRef
name|utf8
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
function_decl|;
comment|/**    * Sorts the entries added in {@link #add(BytesRef)} and returns     * an iterator over all sorted entries.    *     * @throws IOException If an I/O exception occurs.    */
DECL|method|iterator
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

