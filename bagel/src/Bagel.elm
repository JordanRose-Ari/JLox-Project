
module Bagel exposing (..)

import Api.Query as Query
import Browser
import Dict exposing (Dict)
import Graphql.Http exposing (..)
import Graphql.Http.GraphqlError exposing (PossiblyParsedData(..))
import Graphql.Operation exposing (RootQuery)
import Graphql.OptionalArgument exposing (OptionalArgument(..))
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, pre, text, textarea)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import RemoteData exposing (RemoteData)

type alias RemoteOutput =
    RemoteData (Graphql.Http.Error RunResponse) RunResponse

type alias RunResponse =
    String

type alias SignResponse =
    String

main =
    Browser.element
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }

type Msg
    = Run
    | Sign
    | ChangeText String
    | ChangeInput String
    | GotRunResponse (RemoteData (Graphql.Http.Error RunResponse) RunResponse)
    | GotSignResponse (RemoteData (Graphql.Http.Error SignResponse) SignResponse)

type alias Model =
    { code : String
    , output : RemoteData (Graphql.Http.Error RunResponse) RunResponse
    , input : String
    }


type alias Flags =
    ()


init : Flags -> ( Model, Cmd Msg )
init _ =
    ( { code = ""
      , output = RemoteData.NotAsked
      , input = ""
      }
    , Cmd.none
    )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GotSignResponse response ->
            ( { model | output = response }, Cmd.none )

        GotRunResponse response ->
            ( { model | output = response }, Cmd.none )

        ChangeText s ->
            ( { model | code = s }, Cmd.none )

        ChangeInput s ->
            ( { model | input = s }, Cmd.none )

        Run ->
            ( model, makeRunRequest model )

        Sign ->
            ( model, makeSignRequest model )


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none


view : Model -> Html Msg
view model =
    div []
        [ textarea
            [ value model.code, onInput ChangeText, cols 60, rows 24 ]
            []
        , div []
            [ button [ onClick Run ] [ text "Run" ]
            , button [ onClick Sign ] [ text "Sign" ]
            ]
        , div []
            [ pre [] [ viewOutput model.output ]
            ]
        , div []
            [ input [ placeholder "Input", value model.input, onInput ChangeInput ] [] ]
        ]


viewOutput : RemoteOutput -> Html Msg
viewOutput output =
    case output of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            text "Running..."

        RemoteData.Success r ->
            text r

        RemoteData.Failure e ->
            case e of
                HttpError NetworkError ->
                    text "Network error"

                GraphqlError (ParsedData data) _ ->
                    text "GraphQL error"

                GraphqlError _ errors ->
                    text (List.map (\x -> x.message) errors |> String.concat)

                _ ->
                    text "Other Error"


runQuery : Model -> SelectionSet RunResponse RootQuery
runQuery model =
    Query.run (\_ -> { input = Present model.input }) { code = model.code }


signQuery : Model -> SelectionSet SignResponse RootQuery
signQuery model =
    Query.sign { code = model.code }

makeRunRequest : Model -> Cmd Msg
makeRunRequest model =
    runQuery model
        |> Graphql.Http.queryRequest "http://localhost:8080/graphql"
        |> Graphql.Http.send (RemoteData.fromResult >> GotRunResponse)

makeSignRequest : Model -> Cmd Msg
makeSignRequest model =
    signQuery model
        |> Graphql.Http.queryRequest "http://localhost:8080/graphql"
        |> Graphql.Http.send (RemoteData.fromResult >> GotSignResponse)