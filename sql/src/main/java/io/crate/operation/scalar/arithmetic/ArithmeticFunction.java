/*
  * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
  * license agreements.  See the NOTICE file distributed with this work for
  * additional information regarding copyright ownership.  Crate licenses
  * this file to you under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.  You may
  * obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
  * License for the specific language governing permissions and limitations
  * under the License.
  *
  * However, if you have executed another commercial license agreement
  * with Crate these terms will supersede the license and you may use the
  * software solely pursuant to the terms of the relevant commercial agreement.
  */

package io.crate.operation.scalar.arithmetic;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.crate.metadata.FunctionIdent;
import io.crate.metadata.FunctionInfo;
import io.crate.metadata.Scalar;
import io.crate.operation.Input;
import io.crate.planner.symbol.Function;
import io.crate.planner.symbol.Literal;
import io.crate.planner.symbol.Symbol;
import io.crate.types.DataType;
import io.crate.types.DataTypes;

import java.util.List;
import java.util.Set;

abstract class ArithmeticFunction implements Scalar<Number, Number> {

    private final static Set<DataType> NUMERIC_WITH_DECIMAL =
            Sets.<DataType>newHashSet(DataTypes.FLOAT, DataTypes.DOUBLE);

    protected final FunctionInfo info;

    public ArithmeticFunction(FunctionInfo info) {
        this.info = info;
    }

    @Override
    public FunctionInfo info() {
        return info;
    }

    @SuppressWarnings("unchecked")
    public Symbol normalizeSymbol(Function symbol) {
         assert symbol.arguments().size() == 2;

         Symbol left = symbol.arguments().get(0);
         Symbol right = symbol.arguments().get(1);

         if (left.symbolType().isValueSymbol() && right.symbolType().isValueSymbol()) {
             return Literal.newLiteral(info.returnType(), evaluate(new Input[]{(Input) left, (Input) right}));
         }
         return symbol;
    }

    protected static FunctionInfo genDoubleInfo(String functionName, List<DataType> dataTypes) {
             return new FunctionInfo(new FunctionIdent(functionName, dataTypes), DataTypes.DOUBLE);
    }

    protected static FunctionInfo genLongInfo(String functionName, List<DataType> dataTypes) {
        return new FunctionInfo(new FunctionIdent(functionName, dataTypes), DataTypes.LONG);
    }

    protected static void validateTypes(List<DataType> dataTypes) {
        Preconditions.checkArgument(dataTypes.size() == 2);
        DataType leftType = dataTypes.get(0);
        DataType rightType = dataTypes.get(1);
        Preconditions.checkArgument(DataTypes.NUMERIC_PRIMITIVE_TYPES.contains(leftType));
        Preconditions.checkArgument(DataTypes.NUMERIC_PRIMITIVE_TYPES.contains(rightType));
    }

    protected static boolean containsTypesWithDecimal(List<DataType> dataTypes) {
        for (DataType dataType : dataTypes) {
            if (NUMERIC_WITH_DECIMAL.contains(dataType)) {
                return true;
            }
        }
        return false;
    }
}