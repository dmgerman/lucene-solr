begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
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
name|javax
operator|.
name|servlet
operator|.
name|ServletOutputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|WriteListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * Provides a convenient extension of the {@link ServletOutputStream} class that can be subclassed by developers wishing  * to adapt the behavior of a Stream. One such example may be to override {@link #close()} to instead be a no-op as in  * SOLR-8933.  *  * This class implements the Wrapper or Decorator pattern. Methods default to calling through to the wrapped stream.  */
end_comment

begin_class
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"delegate methods"
argument_list|)
DECL|class|ServletOutputStreamWrapper
specifier|public
class|class
name|ServletOutputStreamWrapper
extends|extends
name|ServletOutputStream
block|{
DECL|field|stream
specifier|final
name|ServletOutputStream
name|stream
decl_stmt|;
DECL|method|ServletOutputStreamWrapper
specifier|public
name|ServletOutputStreamWrapper
parameter_list|(
name|ServletOutputStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|stream
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|stream
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|isReady
specifier|public
name|boolean
name|isReady
parameter_list|()
block|{
return|return
name|stream
operator|.
name|isReady
argument_list|()
return|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|boolean
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|double
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|float
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|public
name|void
name|print
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|print
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|boolean
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|double
name|d
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|float
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|public
name|void
name|println
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setWriteListener
specifier|public
name|void
name|setWriteListener
parameter_list|(
name|WriteListener
name|arg0
parameter_list|)
block|{
name|stream
operator|.
name|setWriteListener
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|stream
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

