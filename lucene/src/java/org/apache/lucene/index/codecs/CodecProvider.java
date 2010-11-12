begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|index
operator|.
name|codecs
operator|.
name|preflex
operator|.
name|PreFlexCodec
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
name|index
operator|.
name|codecs
operator|.
name|pulsing
operator|.
name|PulsingCodec
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
name|index
operator|.
name|codecs
operator|.
name|simpletext
operator|.
name|SimpleTextCodec
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
name|index
operator|.
name|codecs
operator|.
name|standard
operator|.
name|StandardCodec
import|;
end_import

begin_comment
comment|/** Holds a set of codecs, keyed by name.  You subclass  *  this, instantiate it, and register your codecs, then  *  pass this instance to IndexReader/IndexWriter (via  *  package private APIs) to use different codecs when  *  reading& writing segments.   *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|CodecProvider
specifier|public
class|class
name|CodecProvider
block|{
DECL|field|infosWriter
specifier|private
name|SegmentInfosWriter
name|infosWriter
init|=
operator|new
name|DefaultSegmentInfosWriter
argument_list|()
decl_stmt|;
DECL|field|infosReader
specifier|private
name|SegmentInfosReader
name|infosReader
init|=
operator|new
name|DefaultSegmentInfosReader
argument_list|()
decl_stmt|;
DECL|field|defaultFieldCodec
specifier|private
name|String
name|defaultFieldCodec
init|=
name|defaultCodec
decl_stmt|;
DECL|field|perFieldMap
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|perFieldMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Codec
argument_list|>
name|codecs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Codec
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|knownExtensions
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|knownExtensions
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|defaultCodec
specifier|private
specifier|static
name|String
name|defaultCodec
init|=
literal|"Standard"
decl_stmt|;
DECL|field|CORE_CODECS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|CORE_CODECS
init|=
operator|new
name|String
index|[]
block|{
literal|"Standard"
block|,
literal|"Pulsing"
block|,
literal|"PreFlex"
block|,
literal|"SimpleText"
block|}
decl_stmt|;
DECL|method|register
specifier|public
specifier|synchronized
name|void
name|register
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
if|if
condition|(
name|codec
operator|.
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"code.name is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|codecs
operator|.
name|containsKey
argument_list|(
name|codec
operator|.
name|name
argument_list|)
condition|)
block|{
name|codecs
operator|.
name|put
argument_list|(
name|codec
operator|.
name|name
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|codec
operator|.
name|getExtensions
argument_list|(
name|knownExtensions
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|codecs
operator|.
name|get
argument_list|(
name|codec
operator|.
name|name
argument_list|)
operator|!=
name|codec
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec '"
operator|+
name|codec
operator|.
name|name
operator|+
literal|"' is already registered as a different codec instance"
argument_list|)
throw|;
block|}
block|}
comment|/** @lucene.internal */
DECL|method|unregister
specifier|public
specifier|synchronized
name|void
name|unregister
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
if|if
condition|(
name|codec
operator|.
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"code.name is null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|codecs
operator|.
name|containsKey
argument_list|(
name|codec
operator|.
name|name
argument_list|)
condition|)
block|{
name|Codec
name|c
init|=
name|codecs
operator|.
name|get
argument_list|(
name|codec
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
name|c
condition|)
block|{
name|codecs
operator|.
name|remove
argument_list|(
name|codec
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec '"
operator|+
name|codec
operator|.
name|name
operator|+
literal|"' is being impersonated by a different codec instance!!!"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getAllExtensions
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getAllExtensions
parameter_list|()
block|{
return|return
name|knownExtensions
return|;
block|}
DECL|method|lookup
specifier|public
specifier|synchronized
name|Codec
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Codec
name|codec
init|=
name|codecs
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"required codec '"
operator|+
name|name
operator|+
literal|"' not found"
argument_list|)
throw|;
return|return
name|codec
return|;
block|}
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfosWriter
name|getSegmentInfosWriter
parameter_list|()
block|{
return|return
name|infosWriter
return|;
block|}
DECL|method|getSegmentInfosReader
specifier|public
name|SegmentInfosReader
name|getSegmentInfosReader
parameter_list|()
block|{
return|return
name|infosReader
return|;
block|}
DECL|field|defaultCodecs
specifier|static
specifier|private
specifier|final
name|CodecProvider
name|defaultCodecs
init|=
operator|new
name|DefaultCodecProvider
argument_list|()
decl_stmt|;
DECL|method|getDefault
specifier|public
specifier|static
name|CodecProvider
name|getDefault
parameter_list|()
block|{
return|return
name|defaultCodecs
return|;
block|}
comment|/** Used for testing. @lucene.internal */
DECL|method|setDefaultCodec
specifier|public
specifier|synchronized
specifier|static
name|void
name|setDefaultCodec
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|defaultCodec
operator|=
name|s
expr_stmt|;
block|}
comment|/** Used for testing. @lucene.internal */
DECL|method|getDefaultCodec
specifier|public
specifier|synchronized
specifier|static
name|String
name|getDefaultCodec
parameter_list|()
block|{
return|return
name|defaultCodec
return|;
block|}
comment|/**    * Sets the {@link Codec} for a given field. Not that setting a fields code is    * write-once. If the fields codec is already set this method will throw an    * {@link IllegalArgumentException}    *     * @param field    *          the name of the field    * @param codec    *          the name of the codec    * @throws IllegalArgumentException    *           if the codec for the given field is already set    *     */
DECL|method|setFieldCodec
specifier|public
specifier|synchronized
name|void
name|setFieldCodec
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|codec
parameter_list|)
block|{
if|if
condition|(
name|perFieldMap
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec for field: "
operator|+
name|field
operator|+
literal|" already set to "
operator|+
name|perFieldMap
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
throw|;
name|perFieldMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the {@link Codec} name for the given field or the default codec if    * not set.    *     * @param name    *          the fields name    * @return the {@link Codec} name for the given field or the default codec if    *         not set.    */
DECL|method|getFieldCodec
specifier|public
specifier|synchronized
name|String
name|getFieldCodec
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|String
name|codec
decl_stmt|;
if|if
condition|(
operator|(
name|codec
operator|=
name|perFieldMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|)
operator|==
literal|null
condition|)
block|{
return|return
name|defaultFieldCodec
return|;
block|}
return|return
name|codec
return|;
block|}
comment|/**    * Returns the default {@link Codec} for this {@link CodecProvider}    *     * @return the default {@link Codec} for this {@link CodecProvider}    */
DECL|method|getDefaultFieldCodec
specifier|public
specifier|synchronized
name|String
name|getDefaultFieldCodec
parameter_list|()
block|{
return|return
name|defaultFieldCodec
return|;
block|}
comment|/**    * Sets the default {@link Codec} for this {@link CodecProvider}    *     * @param codec    *          the codecs name    */
DECL|method|setDefaultFieldCodec
specifier|public
specifier|synchronized
name|void
name|setDefaultFieldCodec
parameter_list|(
name|String
name|codec
parameter_list|)
block|{
name|defaultFieldCodec
operator|=
name|codec
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|DefaultCodecProvider
class|class
name|DefaultCodecProvider
extends|extends
name|CodecProvider
block|{
DECL|method|DefaultCodecProvider
name|DefaultCodecProvider
parameter_list|()
block|{
name|register
argument_list|(
operator|new
name|StandardCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PreFlexCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PulsingCodec
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|SimpleTextCodec
argument_list|()
argument_list|)
expr_stmt|;
name|setDefaultFieldCodec
argument_list|(
name|CodecProvider
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

