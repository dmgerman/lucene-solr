begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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

begin_comment
comment|/**  * Creates the same document each time {@link #getNextDocData(DocData)} is called.  */
end_comment

begin_class
DECL|class|SingleDocSource
specifier|public
class|class
name|SingleDocSource
extends|extends
name|ContentSource
block|{
DECL|field|docID
specifier|private
name|int
name|docID
init|=
literal|0
decl_stmt|;
DECL|field|DOC_TEXT
specifier|static
specifier|final
name|String
name|DOC_TEXT
init|=
literal|"Well, this is just some plain text we use for creating the "
operator|+
literal|"test documents. It used to be a text from an online collection "
operator|+
literal|"devoted to first aid, but if there was there an (online) lawyers "
operator|+
literal|"first aid collection with legal advices, \"it\" might have quite "
operator|+
literal|"probably advised one not to include \"it\"'s text or the text of "
operator|+
literal|"any other online collection in one's code, unless one has money "
operator|+
literal|"that one don't need and one is happy to donate for lawyers "
operator|+
literal|"charity. Anyhow at some point, rechecking the usage of this text, "
operator|+
literal|"it became uncertain that this text is free to use, because "
operator|+
literal|"the web site in the disclaimer of he eBook containing that text "
operator|+
literal|"was not responding anymore, and at the same time, in projGut, "
operator|+
literal|"searching for first aid no longer found that eBook as well. "
operator|+
literal|"So here we are, with a perhaps much less interesting "
operator|+
literal|"text for the test, but oh much much safer. "
decl_stmt|;
comment|// return a new docid
DECL|method|newdocid
specifier|private
specifier|synchronized
name|int
name|newdocid
parameter_list|()
throws|throws
name|NoMoreDataException
block|{
if|if
condition|(
name|docID
operator|>
literal|0
operator|&&
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
return|return
name|docID
operator|++
return|;
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
block|{}
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
block|{
name|int
name|id
init|=
name|newdocid
argument_list|()
decl_stmt|;
name|addBytes
argument_list|(
name|DOC_TEXT
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|docData
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docData
operator|.
name|setName
argument_list|(
literal|"doc"
operator|+
name|id
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
name|DOC_TEXT
argument_list|)
expr_stmt|;
return|return
name|docData
return|;
block|}
annotation|@
name|Override
DECL|method|resetInputs
specifier|public
specifier|synchronized
name|void
name|resetInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|docID
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

