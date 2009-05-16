begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package

begin_class
DECL|class|SegTokenPair
specifier|public
class|class
name|SegTokenPair
block|{
DECL|field|charArray
specifier|public
name|char
index|[]
name|charArray
decl_stmt|;
comment|/**    * fromåtoæ¯Tokenå¯¹çindexå·ï¼è¡¨ç¤ºæ¬TokenPairçä¸¤ä¸ªTokenå¨segGraghä¸­çä½ç½®ã    */
DECL|field|from
specifier|public
name|int
name|from
decl_stmt|;
DECL|field|to
specifier|public
name|int
name|to
decl_stmt|;
DECL|field|weight
specifier|public
name|double
name|weight
decl_stmt|;
DECL|method|SegTokenPair
specifier|public
name|SegTokenPair
parameter_list|(
name|char
index|[]
name|idArray
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|,
name|double
name|weight
parameter_list|)
block|{
name|this
operator|.
name|charArray
operator|=
name|idArray
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|// public String toString() {
comment|// return String.valueOf(charArray) + ":f(" + from + ")t(" + to + "):"
comment|// + weight;
comment|// }
comment|// public boolean equals(SegTokenPair tp) {
comment|// return this.from == tp.from&& this.to == tp.to;
comment|// }
block|}
end_class

end_unit

