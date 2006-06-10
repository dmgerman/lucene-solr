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
comment|/**  * Created by IntelliJ IDEA.  * User: Grant Ingersoll  * Date: Jan 12, 2006  * Time: 9:37:43 AM  * $Id:$  * Copyright 2005.  Center For Natural Language Processing  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|FieldReaderException
specifier|public
class|class
name|FieldReaderException
extends|extends
name|RuntimeException
block|{
comment|/**    * Constructs a new runtime exception with<code>null</code> as its    * detail message.  The cause is not initialized, and may subsequently be    * initialized by a call to {@link #initCause}.    */
DECL|method|FieldReaderException
specifier|public
name|FieldReaderException
parameter_list|()
block|{   }
comment|/**    * Constructs a new runtime exception with the specified cause and a    * detail message of<tt>(cause==null ? null : cause.toString())</tt>    * (which typically contains the class and detail message of    *<tt>cause</tt>).  This constructor is useful for runtime exceptions    * that are little more than wrappers for other throwables.    *    * @param cause the cause (which is saved for later retrieval by the    *              {@link #getCause()} method).  (A<tt>null</tt> value is    *              permitted, and indicates that the cause is nonexistent or    *              unknown.)    * @since 1.4    */
DECL|method|FieldReaderException
specifier|public
name|FieldReaderException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new runtime exception with the specified detail message.    * The cause is not initialized, and may subsequently be initialized by a    * call to {@link #initCause}.    *    * @param message the detail message. The detail message is saved for    *                later retrieval by the {@link #getMessage()} method.    */
DECL|method|FieldReaderException
specifier|public
name|FieldReaderException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new runtime exception with the specified detail message and    * cause.<p>Note that the detail message associated with    *<code>cause</code> is<i>not</i> automatically incorporated in    * this runtime exception's detail message.    *    * @param message the detail message (which is saved for later retrieval    *                by the {@link #getMessage()} method).    * @param cause   the cause (which is saved for later retrieval by the    *                {@link #getCause()} method).  (A<tt>null</tt> value is    *                permitted, and indicates that the cause is nonexistent or    *                unknown.)    * @since 1.4    */
DECL|method|FieldReaderException
specifier|public
name|FieldReaderException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

