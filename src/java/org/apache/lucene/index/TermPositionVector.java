begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/** Extends<code>TermFreqVector</code> to provide additional information about  *  positions in which each of the terms is found.  */
end_comment

begin_interface
DECL|interface|TermPositionVector
specifier|public
interface|interface
name|TermPositionVector
extends|extends
name|TermFreqVector
block|{
comment|/** Returns an array of positions in which the term is found.      *  Terms are identified by the index at which its number appears in the      *  term number array obtained from<code>getTermNumbers</code> method.      */
DECL|method|getTermPositions
specifier|public
name|int
index|[]
name|getTermPositions
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

