begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Marker interface to signal that elements coming from {@link Iterator}  * come in ascending lexicographic order.  */
end_comment

begin_interface
DECL|interface|SortedIterator
specifier|public
interface|interface
name|SortedIterator
block|{  }
end_interface

end_unit

