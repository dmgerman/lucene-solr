begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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

begin_comment
comment|/** Expert: an enumeration of span matches.  Used to implement span searching.  * Each span represents a range of term positions within a document.  Matches  * are enumerated in order, by increasing document number, within that by  * increasing start position and finally by increasing end position. */
end_comment

begin_interface
DECL|interface|Spans
specifier|public
interface|interface
name|Spans
block|{
comment|/** Move to the next match, returning true iff any such exists. */
DECL|method|next
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Skips to the first match beyond the current whose document number is    * greater than or equal to<i>target</i>.<p>Returns true iff there is such    * a match.<p>Behaves as if written:<pre>    *   boolean skipTo(int target) {    *     do {    *       if (!next())    * 	     return false;    *     } while (target> doc());    *     return true;    *   }    *</pre>    * Most implementations are considerably more efficient than that.    */
DECL|method|skipTo
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the document number of the current match.  Initially invalid. */
DECL|method|doc
name|int
name|doc
parameter_list|()
function_decl|;
comment|/** Returns the start position of the current match.  Initially invalid. */
DECL|method|start
name|int
name|start
parameter_list|()
function_decl|;
comment|/** Returns the end position of the current match.  Initially invalid. */
DECL|method|end
name|int
name|end
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

