begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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
name|DataInput
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
name|DataOutput
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
name|InputStreamDataInput
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
name|OutputStreamDataOutput
import|;
end_import

begin_comment
comment|/** Simple point-to-point TCP connection */
end_comment

begin_class
DECL|class|Connection
class|class
name|Connection
implements|implements
name|Closeable
block|{
DECL|field|in
specifier|public
specifier|final
name|DataInput
name|in
decl_stmt|;
DECL|field|out
specifier|public
specifier|final
name|DataOutput
name|out
decl_stmt|;
DECL|field|sockIn
specifier|public
specifier|final
name|InputStream
name|sockIn
decl_stmt|;
DECL|field|bos
specifier|public
specifier|final
name|BufferedOutputStream
name|bos
decl_stmt|;
DECL|field|s
specifier|public
specifier|final
name|Socket
name|s
decl_stmt|;
DECL|field|destTCPPort
specifier|public
specifier|final
name|int
name|destTCPPort
decl_stmt|;
DECL|field|lastKeepAliveNS
specifier|public
name|long
name|lastKeepAliveNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
DECL|method|Connection
specifier|public
name|Connection
parameter_list|(
name|int
name|tcpPort
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|destTCPPort
operator|=
name|tcpPort
expr_stmt|;
name|this
operator|.
name|s
operator|=
operator|new
name|Socket
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|,
name|tcpPort
argument_list|)
expr_stmt|;
name|this
operator|.
name|sockIn
operator|=
name|s
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
name|this
operator|.
name|in
operator|=
operator|new
name|InputStreamDataInput
argument_list|(
name|sockIn
argument_list|)
expr_stmt|;
name|this
operator|.
name|bos
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
name|s
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|bos
argument_list|)
expr_stmt|;
if|if
condition|(
name|Node
operator|.
name|VERBOSE_CONNECTIONS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"make new client Connection socket="
operator|+
name|this
operator|.
name|s
operator|+
literal|" destPort="
operator|+
name|tcpPort
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|bos
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
