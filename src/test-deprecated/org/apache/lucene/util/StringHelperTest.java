begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|StringHelperTest
specifier|public
class|class
name|StringHelperTest
extends|extends
name|TestCase
block|{
DECL|method|StringHelperTest
specifier|public
name|StringHelperTest
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{   }
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|testStringDifference
specifier|public
name|void
name|testStringDifference
parameter_list|()
block|{
name|String
name|test1
init|=
literal|"test"
decl_stmt|;
name|String
name|test2
init|=
literal|"testing"
decl_stmt|;
name|int
name|result
init|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|4
argument_list|)
expr_stmt|;
name|test2
operator|=
literal|"foo"
expr_stmt|;
name|result
operator|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|0
argument_list|)
expr_stmt|;
name|test2
operator|=
literal|"test"
expr_stmt|;
name|result
operator|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

