begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|NamedList
import|;
end_import

begin_class
DECL|class|TestRTimerTree
specifier|public
class|class
name|TestRTimerTree
extends|extends
name|LuceneTestCase
block|{
DECL|class|MockTimerImpl
specifier|private
specifier|static
class|class
name|MockTimerImpl
implements|implements
name|RTimer
operator|.
name|TimerImpl
block|{
DECL|field|systemTime
specifier|static
specifier|private
name|long
name|systemTime
decl_stmt|;
DECL|method|incrementSystemTime
specifier|static
specifier|public
name|void
name|incrementSystemTime
parameter_list|(
name|long
name|ms
parameter_list|)
block|{
name|systemTime
operator|+=
name|ms
expr_stmt|;
block|}
DECL|field|start
specifier|private
name|long
name|start
decl_stmt|;
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|start
operator|=
name|systemTime
expr_stmt|;
block|}
DECL|method|elapsed
specifier|public
name|double
name|elapsed
parameter_list|()
block|{
return|return
name|systemTime
operator|-
name|start
return|;
block|}
block|}
DECL|class|MockRTimerTree
specifier|private
class|class
name|MockRTimerTree
extends|extends
name|RTimerTree
block|{
annotation|@
name|Override
DECL|method|newTimerImpl
specifier|protected
name|TimerImpl
name|newTimerImpl
parameter_list|()
block|{
return|return
operator|new
name|MockTimerImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newTimer
specifier|protected
name|RTimerTree
name|newTimer
parameter_list|()
block|{
return|return
operator|new
name|MockRTimerTree
argument_list|()
return|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|RTimerTree
name|rt
init|=
operator|new
name|MockRTimerTree
argument_list|()
decl_stmt|,
name|subt
decl_stmt|,
name|st
decl_stmt|;
name|MockTimerImpl
operator|.
name|incrementSystemTime
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
operator|(
name|int
operator|)
name|rt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|subt
operator|=
name|rt
operator|.
name|sub
argument_list|(
literal|"sub1"
argument_list|)
expr_stmt|;
name|MockTimerImpl
operator|.
name|incrementSystemTime
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|150
argument_list|,
operator|(
name|int
operator|)
name|rt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
operator|(
name|int
operator|)
name|subt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|st
operator|=
name|subt
operator|.
name|sub
argument_list|(
literal|"sub1.1"
argument_list|)
expr_stmt|;
name|st
operator|.
name|resume
argument_list|()
expr_stmt|;
name|MockTimerImpl
operator|.
name|incrementSystemTime
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|int
operator|)
name|st
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|st
operator|.
name|pause
argument_list|()
expr_stmt|;
name|MockTimerImpl
operator|.
name|incrementSystemTime
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
operator|(
name|int
operator|)
name|st
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|st
operator|.
name|resume
argument_list|()
expr_stmt|;
name|MockTimerImpl
operator|.
name|incrementSystemTime
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|st
operator|.
name|pause
argument_list|()
expr_stmt|;
name|subt
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rt
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
operator|(
name|int
operator|)
name|st
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|120
argument_list|,
operator|(
name|int
operator|)
name|subt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|220
argument_list|,
operator|(
name|int
operator|)
name|rt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
name|rt
operator|.
name|asNamedList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|220
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"time"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
name|sub1nl
init|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"sub1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sub1nl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|120
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|sub1nl
operator|.
name|get
argument_list|(
literal|"time"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
name|sub11nl
init|=
operator|(
name|NamedList
operator|)
name|sub1nl
operator|.
name|get
argument_list|(
literal|"sub1.1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sub11nl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|sub11nl
operator|.
name|get
argument_list|(
literal|"time"
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

