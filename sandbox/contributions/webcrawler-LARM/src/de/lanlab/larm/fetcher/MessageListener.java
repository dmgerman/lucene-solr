begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  LARM - LANLab Retrieval Machine  *  *  $history: $  *  *  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_comment
comment|/**  * A Message Listener works on messages in a message queue Usually it returns  * the message back into the queue. But it can also change the message or create  * a new object. If it returns null, the message handler stops  *  * @author    Administrator  * @created   24. November 2001  */
end_comment

begin_interface
DECL|interface|MessageListener
specifier|public
interface|interface
name|MessageListener
block|{
comment|/**      * the handler      *      * @param message  the message to be handled      * @return         Message  usually the original message      *                 null: the message was consumed      */
DECL|method|handleRequest
specifier|public
name|Message
name|handleRequest
parameter_list|(
name|Message
name|message
parameter_list|)
function_decl|;
comment|/**      * will be called as soon as the Listener is added to the Message Queue      *      * @param handler  the Message Handler      */
DECL|method|notifyAddedToMessageHandler
specifier|public
name|void
name|notifyAddedToMessageHandler
parameter_list|(
name|MessageHandler
name|handler
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

