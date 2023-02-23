-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module Api.Object.If exposing (..)

import Api.InputObject
import Api.Interface
import Api.Object
import Api.Scalar
import Api.ScalarCodecs
import Api.Union
import Graphql.Internal.Builder.Argument as Argument exposing (Argument)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode as Encode exposing (Value)
import Graphql.Operation exposing (RootMutation, RootQuery, RootSubscription)
import Graphql.OptionalArgument exposing (OptionalArgument(..))
import Graphql.SelectionSet exposing (SelectionSet)
import Json.Decode as Decode


id : SelectionSet Int Api.Object.If
id =
    Object.selectionForField "Int" "id" [] Decode.int


condition :
    SelectionSet decodesTo Api.Interface.Expr
    -> SelectionSet decodesTo Api.Object.If
condition object____ =
    Object.selectionForCompositeField "condition" [] object____ Basics.identity


then_ :
    SelectionSet decodesTo Api.Interface.Stmt
    -> SelectionSet decodesTo Api.Object.If
then_ object____ =
    Object.selectionForCompositeField "then" [] object____ Basics.identity


else_ :
    SelectionSet decodesTo Api.Interface.Stmt
    -> SelectionSet (Maybe decodesTo) Api.Object.If
else_ object____ =
    Object.selectionForCompositeField "else" [] object____ (Basics.identity >> Decode.nullable)
