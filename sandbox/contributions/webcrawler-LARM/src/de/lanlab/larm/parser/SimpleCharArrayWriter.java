begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|de.lanlab.larm.parser
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|CharArrayWriter
import|;
end_import

begin_comment
comment|/**  *<p>Title:</p>  *<p>Description:</p>  *<p>Copyright: Copyright (c) 2002</p>  *<p>Company:</p>  * @author unascribed  * @version 1.0  */
end_comment

begin_class
DECL|class|SimpleCharArrayWriter
specifier|public
specifier|final
class|class
name|SimpleCharArrayWriter
extends|extends
name|java
operator|.
name|io
operator|.
name|CharArrayWriter
block|{
DECL|method|SimpleCharArrayWriter
specifier|public
name|SimpleCharArrayWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|SimpleCharArrayWriter
specifier|public
name|SimpleCharArrayWriter
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|// use only to *decrement* size
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|int
name|size
parameter_list|)
block|{
comment|// synchronized (lock) {
if|if
condition|(
name|size
operator|<
name|count
condition|)
name|count
operator|=
name|size
expr_stmt|;
comment|// }
block|}
DECL|method|getCharArray
specifier|public
name|char
index|[]
name|getCharArray
parameter_list|()
block|{
comment|// synchronized (lock) {
return|return
name|buf
return|;
comment|// }
block|}
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

