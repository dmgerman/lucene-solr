begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title:        LARM Lanlab Retrieval Machine<p>  * Description:<p>  * Copyright:    Copyright (c)<p>  * Company:<p>  * @author  * @version 1.0  */
end_comment

begin_package
DECL|package|de.lanlab.larm.storage
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * doesn't do a lot  */
end_comment

begin_class
DECL|class|NullStorage
specifier|public
class|class
name|NullStorage
implements|implements
name|DocumentStorage
block|{
DECL|method|NullStorage
specifier|public
name|NullStorage
parameter_list|()
block|{     }
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
block|{}
DECL|method|store
specifier|public
name|void
name|store
parameter_list|(
name|WebDocument
name|doc
parameter_list|)
block|{}
block|}
end_class

end_unit

