begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Observable
import|;
end_import

begin_class
DECL|class|SimpleObservable
specifier|public
class|class
name|SimpleObservable
extends|extends
name|Observable
block|{
DECL|method|setChanged
specifier|public
name|void
name|setChanged
parameter_list|()
block|{
name|super
operator|.
name|setChanged
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

