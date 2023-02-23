-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module Api.Interface.Stmt exposing (..)

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
import Graphql.SelectionSet exposing (FragmentSelectionSet(..), SelectionSet(..))
import Json.Decode as Decode


type alias Fragments decodesTo =
    { onFunction : SelectionSet decodesTo Api.Object.Function
    , onClassDecl : SelectionSet decodesTo Api.Object.ClassDecl
    , onExpression : SelectionSet decodesTo Api.Object.Expression
    , onPrint : SelectionSet decodesTo Api.Object.Print
    , onBlock : SelectionSet decodesTo Api.Object.Block
    , onIf : SelectionSet decodesTo Api.Object.If
    , onWhile : SelectionSet decodesTo Api.Object.While
    , onVar : SelectionSet decodesTo Api.Object.Var
    , onReturn : SelectionSet decodesTo Api.Object.Return
    }


{-| Build an exhaustive selection of type-specific fragments.
-}
fragments :
    Fragments decodesTo
    -> SelectionSet decodesTo Api.Interface.Stmt
fragments selections____ =
    Object.exhaustiveFragmentSelection
        [ Object.buildFragment "Function" selections____.onFunction
        , Object.buildFragment "ClassDecl" selections____.onClassDecl
        , Object.buildFragment "Expression" selections____.onExpression
        , Object.buildFragment "Print" selections____.onPrint
        , Object.buildFragment "Block" selections____.onBlock
        , Object.buildFragment "If" selections____.onIf
        , Object.buildFragment "While" selections____.onWhile
        , Object.buildFragment "Var" selections____.onVar
        , Object.buildFragment "Return" selections____.onReturn
        ]


{-| Can be used to create a non-exhaustive set of fragments by using the record
update syntax to add `SelectionSet`s for the types you want to handle.
-}
maybeFragments : Fragments (Maybe decodesTo)
maybeFragments =
    { onFunction = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onClassDecl = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onExpression = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onPrint = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onBlock = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onIf = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onWhile = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onVar = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onReturn = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    }


id : SelectionSet Int Api.Interface.Stmt
id =
    Object.selectionForField "Int" "id" [] Decode.int
