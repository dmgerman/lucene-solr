begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.threads
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
package|;
end_package

begin_class
DECL|class|ThreadFactory
specifier|public
class|class
name|ThreadFactory
block|{
comment|// static int count = 0;
DECL|method|createServerThread
specifier|public
name|ServerThread
name|createServerThread
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
operator|new
name|ServerThread
argument_list|(
name|count
argument_list|)
return|;
block|}
block|}
end_class

end_unit

