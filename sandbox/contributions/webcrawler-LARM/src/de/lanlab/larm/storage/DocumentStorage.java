begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Title: LARM Lanlab Retrieval Machine<p>  *  * Description:<p>  *  * Copyright: Copyright (c)<p>  *  * Company:<p>  *  *  *  * @author  * @version   1.0  */
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
comment|/**  * This interface stores documents provided by a fetcher task  * @author    Clemens Marschner  */
end_comment

begin_interface
DECL|interface|DocumentStorage
specifier|public
interface|interface
name|DocumentStorage
block|{
comment|/**      * called once when the storage is supposed to be initialized      */
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
function_decl|;
comment|/**      * called to store a web document      *      * @param doc  the document      */
DECL|method|store
specifier|public
name|void
name|store
parameter_list|(
name|WebDocument
name|doc
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

