begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_class
DECL|class|RefCntRamDirectory
specifier|public
class|class
name|RefCntRamDirectory
extends|extends
name|RAMDirectory
block|{
DECL|field|refCount
specifier|private
specifier|final
name|AtomicInteger
name|refCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|RefCntRamDirectory
specifier|public
name|RefCntRamDirectory
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|incRef
argument_list|()
expr_stmt|;
block|}
DECL|method|RefCntRamDirectory
specifier|public
name|RefCntRamDirectory
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|()
expr_stmt|;
name|Directory
operator|.
name|copy
argument_list|(
name|dir
argument_list|,
name|this
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|decRef
specifier|public
name|void
name|decRef
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|refCount
operator|.
name|getAndDecrement
argument_list|()
operator|==
literal|1
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
specifier|final
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|isOpen
condition|)
block|{
name|decRef
argument_list|()
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isOpen
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|isOpen
return|;
block|}
block|}
end_class

end_unit

